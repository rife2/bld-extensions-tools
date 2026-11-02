/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rife.bld.extension.tools;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import rife.bld.BaseProject;
import rife.bld.BldVersion;
import rife.bld.dependencies.*;
import rife.tools.FileUtils;
import rife.tools.Product;
import rife.tools.exceptions.FileUtilsErrorException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Manages a per-extension sandbox at {@code lib/.sandbox/<extensionName>} for caching
 * resolved dependencies and downloaded ZIP archives.
 * <p>
 * A download is skipped when a recorded snapshot is still valid: the resolution inputs, the
 * extension name, the JVM version, and the on-disk content of the target directory must all
 * match. Downloads for the same extension are serialized; different extensions may proceed
 * in parallel.
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.4
 */
@NullMarked
@SuppressWarnings("PMD.ExcessiveImports")
public final class Sandbox {

    private static final String DEPENDENCIES = "dependencies";
    /**
     * Extension names: alphanumeric at both ends, only {@code [A-Za-z0-9._-]} inside.
     */
    private static final Pattern EXTENSION_NAME_PATTERN =
            Pattern.compile("[A-Za-z0-9](?:[A-Za-z0-9._-]*[A-Za-z0-9])?");
    private static final String GITIGNORE = ".gitignore";
    /**
     * Upper bound on the total number of bytes extracted from a ZIP; guards against zip bombs.
     */
    private static final long MAX_EXTRACTED_BYTES = 2L * 1024 * 1024 * 1024;
    private static final String REPOSITORIES = "repositories";
    private static final String SANDBOX_DIRECTORY = ".sandbox";
    private static final String SANDBOX_DIR_CREATE_ERROR = "Could not create sandbox directory: ";
    private static final int ZIP_CONNECT_TIMEOUT_MILLIS = 30_000;
    private static final int ZIP_READ_TIMEOUT_MILLIS = 60_000;

    private static final ArtifactRetriever artifactRetriever = ArtifactRetriever.cachingInstance();
    private static final ConcurrentMap<Path, ReentrantLock> directoryLocks = new ConcurrentHashMap<>();
    private static final AtomicReference<Supplier<String>> jvmVersionSupplier_ =
            new AtomicReference<>(() -> Runtime.version().toString());
    private static final Logger logger = Logger.getLogger(Sandbox.class.getName());
    private static final ReentrantLock snapshotLock = new ReentrantLock();
    private final String extensionName_;
    private final Path sandboxDirectory_;
    private final Path sandboxExtensionDirectory_;
    private final Path sandboxSnapshotFile_;

    /**
     * Creates a sandbox for the given extension.
     *
     * @param extensionName the extension name; must start and end with an ASCII alphanumeric
     *                      character and contain only {@code [A-Za-z0-9._-]}
     * @param project       the project used to resolve {@code lib/bld}
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if {@code extensionName} is blank or invalid
     */
    public Sandbox(String extensionName, BaseProject project) {
        ObjectTools.requireNonNull(project, "project");
        extensionName_ = TextTools.requireNotBlank(extensionName, "extensionName");
        requireValidExtensionName(extensionName_);
        // Sibling of lib/bld, not a child: keeps the sandbox off the wrapper's classpath.
        sandboxDirectory_ = project.libBldDirectory().toPath()
                .resolveSibling(SANDBOX_DIRECTORY)
                .toAbsolutePath()
                .normalize();
        sandboxExtensionDirectory_ = sandboxDirectory_.resolve(extensionName_).normalize();
        sandboxSnapshotFile_ = sandboxDirectory_.resolve("sandbox.snapshot");
    }

    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    private static void deleteDirectory(Path directory) {
        if (!Files.exists(directory)) {
            return;
        }
        try {
            FileUtils.deleteDirectory(directory.toFile());
        } catch (FileUtilsErrorException e) {
            throw new IllegalStateException("Could not delete sandbox directory: " + directory, e);
        }
        if (Files.exists(directory)) {
            throw new IllegalStateException("Could not delete sandbox directory: " + directory);
        }
    }

    /**
     * Creates a {@code .gitignore} file in the sandbox's parent directory (e.g. {@code lib})
     * containing {@code .sandbox/}, so that git ignores the sandbox directory. The file is
     * only created if one does not already exist; an existing file is left untouched.
     *
     * @param sandboxDirectory the sandbox root directory
     */
    private static void ensureGitIgnore(Path sandboxDirectory) {
        var parent = sandboxDirectory.getParent();
        if (parent == null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "Skipping git ignore setup: no parent for {0}", sandboxDirectory);
            }
            return;
        }
        var gitIgnore = parent.resolve(GITIGNORE);
        if (Files.exists(gitIgnore)) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Git ignore file already exists: {0}", gitIgnore);
            }
            return;
        }
        try {
            Files.writeString(gitIgnore, SANDBOX_DIRECTORY + "/\n", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Created git ignore file: {0} (ignoring {1}/)",
                        new Object[]{gitIgnore, SANDBOX_DIRECTORY});
            }
        } catch (FileAlreadyExistsException e) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Git ignore file already exists: {0}", gitIgnore);
            }
        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING,
                        "Could not create " + gitIgnore + "; sandbox contents may be tracked by git", e);
            }
        }
    }

    /**
     * Streams a ZIP from {@code url} and extracts every entry into {@code targetDirectory}.
     * Rejects entries that would escape the target directory (zip slip) and aborts if the
     * cumulative uncompressed size exceeds {@link #MAX_EXTRACTED_BYTES}.
     */
    private static void extractZip(URL url, Path targetDirectory) throws IOException {
        var connection = url.openConnection();
        connection.setConnectTimeout(ZIP_CONNECT_TIMEOUT_MILLIS);
        connection.setReadTimeout(ZIP_READ_TIMEOUT_MILLIS);
        connection.setRequestProperty("User-Agent", Product.BLD.toUserAgent(BldVersion.getVersion()));

        long totalExtracted = 0L;
        try (var in = connection.getInputStream();
             var zipIn = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                var name = entry.getName();
                if (name.isEmpty()) {
                    zipIn.closeEntry();
                    continue;
                }
                var target = targetDirectory.resolve(name).normalize();
                if (!target.startsWith(targetDirectory)) {
                    throw new IOException("ZIP entry escapes target directory: " + name);
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                } else {
                    var parent = target.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    long written;
                    try (var out = Files.newOutputStream(target,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE)) {
                        written = zipIn.transferTo(out);
                    }
                    totalExtracted += written;
                    if (totalExtracted > MAX_EXTRACTED_BYTES) {
                        throw new IOException("ZIP content exceeds maximum allowed extracted size ("
                                + MAX_EXTRACTED_BYTES + " bytes)");
                    }
                }
                zipIn.closeEntry();
            }
        }
    }

    private static String getDirectoryHash(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return "";
        }
        var manifest = new StringBuilder();
        // No FOLLOW_LINKS: symlinks hash as links, not as their targets.
        try (var stream = Files.walk(directory)) {
            var sortedPaths = stream.sorted().toList();
            for (Path path : sortedPaths) {
                manifest.append(toPortableString(directory.relativize(path))).append('|');
                if (Files.isRegularFile(path)) {
                    manifest.append(sha256(path)).append('\n');
                } else {
                    manifest.append("DIR\n");
                }
            }
        }
        return sha256(manifest.toString());
    }

    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    private static MessageDigest newSha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    /**
     * Extracts the recorded JVM version from a snapshot value of the form
     * {@code inputsHash:jvmVersion:directoryHash}, or {@code null} if the value does not have
     * that shape (e.g. it was written by an older format).
     */
    private static @Nullable String recordedJvmVersion(String snapshotValue) {
        var firstColon = snapshotValue.indexOf(':');
        if (firstColon < 0) {
            return null;
        }
        var secondColon = snapshotValue.indexOf(':', firstColon + 1);
        if (secondColon < 0) {
            return null;
        }
        return snapshotValue.substring(firstColon + 1, secondColon);
    }

    private static void requireValidExtensionName(String name) {
        if (!EXTENSION_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException(
                    "extensionName must start and end with an alphanumeric character and "
                            + "contain only [A-Za-z0-9._-]: " + name);
        }
    }

    /**
     * Overrides the JVM version supplier for testing; {@code null} restores the default.
     *
     * @param supplier the replacement supplier, or {@code null} to reset
     */
    static void setJvmVersionSupplier(@Nullable Supplier<String> supplier) {
        jvmVersionSupplier_.set((supplier != null) ? supplier : () -> Runtime.version().toString());
    }

    private static String sha256(Path file) throws IOException {
        var digest = newSha256();
        try (var in = new DigestInputStream(Files.newInputStream(file), digest)) {
            in.transferTo(OutputStream.nullOutputStream());
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private static String sha256(String text) {
        return HexFormat.of().formatHex(newSha256().digest(text.getBytes(StandardCharsets.UTF_8)));
    }

    private static String toPortableString(Path path) {
        return path.toString().replace(File.separatorChar, '/');
    }

    /**
     * Downloads the given dependencies using the provided repositories and a default
     * {@link VersionResolution}.
     *
     * @param dependencies the dependencies to resolve
     * @param repositories the repositories to resolve from
     * @return the sandbox directory containing the artifacts
     * @throws NullPointerException     if any argument is {@code null} or contains {@code null}
     *                                  elements, or if the JVM version supplier returns {@code null}
     * @throws IllegalArgumentException if any argument is empty or contains empty elements
     */
    public Path downloadDependencies(List<Dependency> dependencies, List<Repository> repositories) {
        ObjectTools.requireNotEmpty(dependencies, DEPENDENCIES);
        ObjectTools.requireNotEmpty(repositories, REPOSITORIES);
        return downloadDependencies(dependencies, repositories, new VersionResolution(null), (Path) null);
    }

    /**
     * Downloads the given dependencies using the provided repositories and version resolution.
     *
     * @param dependencies the dependencies to resolve
     * @param repositories the repositories to resolve from
     * @param resolution   the version resolution strategy
     * @return the sandbox directory containing the artifacts
     * @throws NullPointerException     if any argument is {@code null} or contains {@code null}
     *                                  elements, or if the JVM version supplier returns {@code null}
     * @throws IllegalArgumentException if any argument is empty or contains empty elements
     */
    public Path downloadDependencies(List<Dependency> dependencies,
                                     List<Repository> repositories,
                                     VersionResolution resolution) {
        ObjectTools.requireNotEmpty(dependencies, DEPENDENCIES);
        ObjectTools.requireNotEmpty(repositories, REPOSITORIES);
        ObjectTools.requireNonNull(resolution, "resolution");
        return downloadDependencies(dependencies, repositories, resolution, (Path) null);
    }

    /**
     * Downloads dependencies into the sandbox, optionally under a subdirectory.
     *
     * @param dependencies the direct dependencies to resolve
     * @param repositories the repositories to resolve from
     * @param resolution   the version resolution strategy
     * @param subDirectory optional subdirectory within the extension sandbox; {@code null}
     *                     uses the extension root
     * @return the directory containing the artifacts
     * @throws NullPointerException     if any argument is {@code null} or contains {@code null}
     *                                  elements, or if the JVM version supplier returns {@code null}
     * @throws IllegalArgumentException if any argument is empty or contains empty elements, or
     *                                  if {@code subDirectory} resolves outside the extension sandbox
     * @throws IllegalStateException    if an existing download directory cannot be deleted
     * @throws UncheckedIOException     if the download directory cannot be created
     */
    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    public Path downloadDependencies(List<Dependency> dependencies,
                                     List<Repository> repositories,
                                     VersionResolution resolution,
                                     @Nullable Path subDirectory) {
        ObjectTools.requireNotEmpty(dependencies, DEPENDENCIES);
        ObjectTools.requireNotEmpty(repositories, REPOSITORIES);
        ObjectTools.requireNonNull(resolution, "resolution");

        var relative = relativeSubDirectory(subDirectory);
        var downloadDirectory = (relative != null)
                ? sandboxExtensionDirectory_.resolve(relative)
                : sandboxExtensionDirectory_;
        var snapshotKey = (relative != null)
                ? extensionName_ + '/' + toPortableString(relative)
                : extensionName_;
        var inputsHash = hashInputs(dependencies, repositories, resolution);

        // Locking on the extension root serializes all downloads for one extension, which
        // guarantees no two snapshot keys touch the same directory concurrently.
        var lock = directoryLocks.computeIfAbsent(sandboxExtensionDirectory_, k -> new ReentrantLock());
        lock.lock();
        try {
            try {
                Files.createDirectories(sandboxDirectory_);
            } catch (IOException e) {
                throw new UncheckedIOException(SANDBOX_DIR_CREATE_ERROR + sandboxDirectory_, e);
            }
            ensureGitIgnore(sandboxDirectory_);

            if (isSnapshotValid(snapshotKey, inputsHash, downloadDirectory)) {
                if (logger.isLoggable(Level.INFO)) {
                    logger.log(Level.INFO, "Reusing valid sandbox snapshot for ''{0}''", snapshotKey);
                }
                return downloadDirectory;
            }

            deleteDirectory(downloadDirectory);

            try {
                Files.createDirectories(downloadDirectory);
            } catch (IOException e) {
                throw new UncheckedIOException(SANDBOX_DIR_CREATE_ERROR + downloadDirectory, e);
            }

            var resolver = new ParallelDependencyResolver(resolution, artifactRetriever, repositories);
            var dependencySet = resolver.resolveAllDependencies(dependencies, Scope.compile, Scope.runtime);

            dependencySet.transferIntoDirectory(
                    resolution,
                    artifactRetriever,
                    repositories,
                    downloadDirectory.toFile(),
                    downloadDirectory.resolve("modules").toFile()
            );

            saveSnapshot(snapshotKey, inputsHash, downloadDirectory);
        } finally {
            lock.unlock();
        }

        return downloadDirectory;
    }

    /**
     * Downloads dependencies into the sandbox, optionally under a {@link File} subdirectory.
     *
     * @param dependencies the dependencies to resolve
     * @param repositories the repositories to resolve from
     * @param resolution   the version resolution strategy
     * @param subDirectory optional subdirectory within the extension sandbox; {@code null}
     *                     uses the extension root
     * @return the directory containing the artifacts
     * @throws NullPointerException     if any argument is {@code null} or contains {@code null}
     *                                  elements, or if the JVM version supplier returns {@code null}
     * @throws IllegalArgumentException if any argument is empty or contains empty elements, or
     *                                  if {@code subDirectory} resolves outside the extension sandbox
     * @throws IllegalStateException    if an existing download directory cannot be deleted
     * @throws UncheckedIOException     if the download directory cannot be created
     */
    public Path downloadDependencies(List<Dependency> dependencies,
                                     List<Repository> repositories,
                                     VersionResolution resolution,
                                     @Nullable File subDirectory) {
        ObjectTools.requireNotEmpty(dependencies, DEPENDENCIES);
        ObjectTools.requireNotEmpty(repositories, REPOSITORIES);
        ObjectTools.requireNonNull(resolution, "resolution");
        return downloadDependencies(dependencies, repositories, resolution,
                subDirectory != null ? subDirectory.toPath() : null);
    }

    /**
     * Downloads dependencies into the sandbox, optionally under a subdirectory.
     *
     * @param dependencies the dependencies to resolve
     * @param repositories the repositories to resolve from
     * @param subDirectory optional subdirectory within the extension sandbox; {@code null}
     *                     uses the extension root
     * @return the directory containing the artifacts
     * @throws NullPointerException     if any argument is {@code null} or contains {@code null}
     *                                  elements, or if the JVM version supplier returns {@code null}
     * @throws IllegalArgumentException if any argument is empty or contains empty elements, or
     *                                  if {@code subDirectory} resolves outside the extension sandbox
     * @throws IllegalStateException    if an existing download directory cannot be deleted
     * @throws UncheckedIOException     if the download directory cannot be created
     */
    public Path downloadDependencies(List<Dependency> dependencies,
                                     List<Repository> repositories,
                                     @Nullable Path subDirectory) {
        ObjectTools.requireNotEmpty(dependencies, DEPENDENCIES);
        ObjectTools.requireNotEmpty(repositories, REPOSITORIES);
        return downloadDependencies(dependencies, repositories, new VersionResolution(null), subDirectory);
    }

    /**
     * Downloads dependencies into the sandbox, optionally under a {@link File} subdirectory.
     *
     * @param dependencies the dependencies to resolve
     * @param repositories the repositories to resolve from
     * @param subDirectory optional subdirectory within the extension sandbox; {@code null}
     *                     uses the extension root
     * @return the directory containing the artifacts
     * @throws NullPointerException     if any argument is {@code null} or contains {@code null}
     *                                  elements, or if the JVM version supplier returns {@code null}
     * @throws IllegalArgumentException if any argument is empty or contains empty elements, or
     *                                  if {@code subDirectory} resolves outside the extension sandbox
     * @throws IllegalStateException    if an existing download directory cannot be deleted
     * @throws UncheckedIOException     if the download directory cannot be created
     */
    public Path downloadDependencies(List<Dependency> dependencies,
                                     List<Repository> repositories,
                                     @Nullable File subDirectory) {
        ObjectTools.requireNotEmpty(dependencies, DEPENDENCIES);
        ObjectTools.requireNotEmpty(repositories, REPOSITORIES);
        return downloadDependencies(dependencies, repositories, new VersionResolution(null),
                subDirectory != null ? subDirectory.toPath() : null);
    }

    /**
     * Downloads a ZIP file from the given URL and extracts it into the extension sandbox
     * directory.
     * <p>
     * Any existing content in the target directory is deleted first. The snapshot is
     * validated against the extension name, the URL, the provided {@code version}, the JVM
     * version, and the on-disk content of the target directory. Callers must change
     * {@code version} whenever the content served at {@code url} changes, so that the stale
     * extraction is invalidated.
     *
     * @param url     the URL of the ZIP file to download
     * @param version a version string for the ZIP content; changing it invalidates the snapshot
     * @return the directory containing the extracted content
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if {@code version} is blank or contains {@code ':'}
     * @throws IllegalStateException    if the target directory cannot be deleted
     * @throws UncheckedIOException     if the ZIP cannot be downloaded or extracted
     */
    public Path downloadZip(URL url, String version) {
        return downloadZip(url, version, (Path) null);
    }

    /**
     * Downloads a ZIP file from the given URL and extracts it into the extension sandbox,
     * optionally under a {@link Path} subdirectory.
     *
     * @param url          the URL of the ZIP file to download
     * @param version      a version string for the ZIP content; changing it invalidates the snapshot
     * @param subDirectory optional subdirectory within the extension sandbox; {@code null}
     *                     uses the extension root
     * @return the directory containing the extracted content
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if {@code version} is blank or contains {@code ':'}, or
     *                                  if {@code subDirectory} resolves outside the extension sandbox
     * @throws IllegalStateException    if the target directory cannot be deleted
     * @throws UncheckedIOException     if the ZIP cannot be downloaded or extracted
     */
    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    public Path downloadZip(URL url, String version, @Nullable Path subDirectory) {
        ObjectTools.requireNonNull(url, "url");
        var validatedVersion = TextTools.requireNotBlank(version, "version");
        if (validatedVersion.indexOf(':') >= 0) {
            throw new IllegalArgumentException("version must not contain ':': " + validatedVersion);
        }

        var relative = relativeSubDirectory(subDirectory);
        var downloadDirectory = (relative != null)
                ? sandboxExtensionDirectory_.resolve(relative)
                : sandboxExtensionDirectory_;
        var snapshotKey = (relative != null)
                ? extensionName_ + '/' + toPortableString(relative)
                : extensionName_;
        var inputsHash = hashZipInputs(url, validatedVersion);

        var lock = directoryLocks.computeIfAbsent(sandboxExtensionDirectory_, k -> new ReentrantLock());
        lock.lock();
        try {
            try {
                Files.createDirectories(sandboxDirectory_);
            } catch (IOException e) {
                throw new UncheckedIOException("Could not create sandbox directory: " + sandboxDirectory_, e);
            }
            ensureGitIgnore(sandboxDirectory_);

            if (isSnapshotValid(snapshotKey, inputsHash, downloadDirectory)) {
                if (logger.isLoggable(Level.INFO)) {
                    logger.log(Level.INFO, "Reusing valid sandbox snapshot for ''{0}''", snapshotKey);
                }
                return downloadDirectory;
            }

            deleteDirectory(downloadDirectory);

            try {
                Files.createDirectories(downloadDirectory);
            } catch (IOException e) {
                throw new UncheckedIOException("Could not create sandbox directory: " + downloadDirectory, e);
            }

            try {
                extractZip(url, downloadDirectory);
            } catch (IOException e) {
                throw new UncheckedIOException("Could not download or extract ZIP: " + url, e);
            }

            saveSnapshot(snapshotKey, inputsHash, downloadDirectory);
        } finally {
            lock.unlock();
        }

        return downloadDirectory;
    }

    /**
     * Downloads a ZIP file from the given URL and extracts it into the extension sandbox,
     * optionally under a {@link File} subdirectory.
     *
     * @param url          the URL of the ZIP file to download
     * @param version      a version string for the ZIP content; changing it invalidates the snapshot
     * @param subDirectory optional subdirectory within the extension sandbox; {@code null}
     *                     uses the extension root
     * @return the directory containing the extracted content
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if {@code version} is blank or contains {@code ':'}, or
     *                                  if {@code subDirectory} resolves outside the extension sandbox
     * @throws IllegalStateException    if the target directory cannot be deleted
     * @throws UncheckedIOException     if the ZIP cannot be downloaded or extracted
     */
    public Path downloadZip(URL url, String version, @Nullable File subDirectory) {
        return downloadZip(url, version, subDirectory != null ? subDirectory.toPath() : null);
    }

    /**
     * Returns the root sandbox directory.
     *
     * @return the path to {@code lib/.sandbox}
     */
    public Path getSandboxDirectory() {
        return sandboxDirectory_;
    }

    /**
     * Returns the root sandbox directory as a {@link File}.
     *
     * @return the root sandbox directory file
     */
    public File getSandboxDirectoryAsFile() {
        return sandboxDirectory_.toFile();
    }

    /**
     * Returns the extension-specific sandbox directory.
     *
     * @return the path to {@code lib/.sandbox/<extensionName>}
     */
    public Path getSandboxExtensionDirectory() {
        return sandboxExtensionDirectory_;
    }

    /**
     * Returns the extension-specific sandbox directory as a {@link File}.
     *
     * @return the extension sandbox directory file
     */
    public File getSandboxExtensionDirectoryAsFile() {
        return sandboxExtensionDirectory_.toFile();
    }

    private String hashInputs(Collection<Dependency> dependencies,
                              Collection<Repository> repositories,
                              VersionResolution resolution) {
        // Sort every section so caller iteration order does not affect the hash.
        var dependenciesSection = new TreeSet<String>();
        for (var dependency : dependencies) {
            dependenciesSection.add(dependency + "|" + dependency.exclusions());
        }
        var repositoriesSection = new TreeSet<String>();
        for (var repository : repositories) {
            repositoriesSection.add(repository.toString());
        }

        var manifest = new StringBuilder(52);
        manifest.append("extension|").append(extensionName_).append('\n');
        for (var line : dependenciesSection) {
            manifest.append("dependency|").append(line).append('\n');
        }
        for (var line : repositoriesSection) {
            manifest.append("repository|").append(line).append('\n');
        }
        manifest.append("overrides|").append(new TreeMap<>(resolution.versionOverrides()))
                .append("\nboms|").append(new TreeMap<>(resolution.bomVersions())).append('\n');
        return sha256(manifest.toString());
    }

    private String hashZipInputs(URL url, String version) {
        var manifest = "extension|" + extensionName_ + '\n' +
                "zip|" + url.toExternalForm() + '\n' +
                "version|" + version + '\n';
        return sha256(manifest);
    }

    /**
     * @throws NullPointerException if the JVM version supplier returns {@code null}
     */
    private boolean isSnapshotValid(String snapshotKey, String inputsHash, Path directory) {
        if (!Files.exists(sandboxSnapshotFile_)) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO,
                        "Invalidating sandbox snapshot for ''{0}'': no snapshot file", snapshotKey);
            }
            return false;
        }
        if (!Files.exists(directory)) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO,
                        "Invalidating sandbox snapshot for ''{0}'': no sandbox directory", snapshotKey);
            }
            return false;
        }
        var snapshot = new Properties();
        try (var in = Files.newInputStream(sandboxSnapshotFile_)) {
            snapshot.load(in);
        } catch (IOException e) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO,
                        "Invalidating sandbox snapshot for ''{0}'': snapshot file unreadable: {1}",
                        new Object[]{snapshotKey, e.getMessage()});
            }
            return false;
        }
        var expected = snapshot.getProperty(snapshotKey);
        if (expected == null) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO,
                        "Invalidating sandbox snapshot for ''{0}'': no recorded entry", snapshotKey);
            }
            return false;
        }
        var currentJvm = jvmVersion();
        var prefix = inputsHash + ':' + currentJvm + ':';
        if (!expected.startsWith(prefix)) {
            var recordedJvm = recordedJvmVersion(expected);
            if (recordedJvm != null && !recordedJvm.equals(currentJvm)) {
                if (logger.isLoggable(Level.INFO)) {
                    logger.log(Level.INFO,
                            "Invalidating sandbox snapshot for ''{0}'': JVM version changed from {1} to {2}",
                            new Object[]{snapshotKey, recordedJvm, currentJvm});
                }
            } else {
                if (logger.isLoggable(Level.INFO)) {
                    logger.log(Level.INFO,
                            "Invalidating sandbox snapshot for ''{0}'': resolution inputs changed",
                            snapshotKey);
                }
            }
            return false;
        }
        try {
            if (!expected.equals(prefix + getDirectoryHash(directory))) {
                if (logger.isLoggable(Level.INFO)) {
                    logger.log(Level.INFO,
                            "Invalidating sandbox snapshot for ''{0}'': sandbox content changed",
                            snapshotKey);
                }
                return false;
            }
        } catch (IOException e) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO,
                        "Invalidating sandbox snapshot for ''{0}'': sandbox content unhashable: {1}",
                        new Object[]{snapshotKey, e.getMessage()});
            }
            return false;
        }
        return true;
    }

    /**
     * Returns the running JVM version used in the snapshot fingerprint.
     *
     * @return the JVM version string
     * @throws NullPointerException if the configured supplier returns {@code null}
     */
    String jvmVersion() {
        return Objects.requireNonNull(jvmVersionSupplier_.get().get(), "jvmVersion supplier returned null");
    }

    private @Nullable Path relativeSubDirectory(@Nullable Path subDirectory) {
        if (subDirectory == null) {
            return null;
        }
        var root = sandboxExtensionDirectory_;
        var resolved = root.resolve(subDirectory).normalize();
        if (!resolved.startsWith(root)) {
            throw new IllegalArgumentException(
                    "subDirectory must be within the extension sandbox: " + subDirectory);
        }
        var relative = root.relativize(resolved);
        return relative.toString().isEmpty() ? null : relative;
    }

    /**
     * Records the fingerprint of a completed download. A read failure aborts (the
     * existing file is left untouched); a write failure is logged and swallowed so the
     * downloaded artifacts remain usable and the next run re-downloads.
     */
    private void saveSnapshot(String snapshotKey, String inputsHash, Path directory) {
        try {
            var value = snapshotValue(inputsHash, directory);
            IOTools.createDirs(sandboxDirectory_);
            snapshotLock.lock();
            try {
                var snapshot = new Properties();
                if (Files.exists(sandboxSnapshotFile_)) {
                    try (var in = Files.newInputStream(sandboxSnapshotFile_)) {
                        snapshot.load(in);
                    }
                }
                snapshot.setProperty(snapshotKey, value);
                writeSnapshot(snapshot);
            } finally {
                snapshotLock.unlock();
            }
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Sandbox snapshot saved for ''{0}''", snapshotKey);
            }
        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING,
                        "Could not save sandbox snapshot; next run will re-download", e);
            }
        }
    }

    private String snapshotValue(String inputsHash, Path directory) throws IOException {
        return inputsHash + ':' + jvmVersion() + ':' + getDirectoryHash(directory);
    }

    private void writeSnapshot(Properties snapshot) throws IOException {
        var temp = Files.createTempFile(sandboxDirectory_, "sandbox", ".snapshot.tmp");
        try (var out = Files.newOutputStream(temp)) {
            snapshot.store(out, "Sandbox snapshots");
        }
        try {
            Files.move(temp, sandboxSnapshotFile_, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, sandboxSnapshotFile_, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}