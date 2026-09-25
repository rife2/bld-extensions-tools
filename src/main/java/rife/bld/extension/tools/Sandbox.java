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
import rife.bld.dependencies.*;
import rife.tools.FileUtils;
import rife.tools.exceptions.FileUtilsErrorException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages an isolated, per-extension sandbox for downloading dependencies, rooted at
 * {@code lib/bld/.sandbox/<extensionName>}.
 * <p>
 * Dependencies can be downloaded into the extension's sandbox root or into an explicit
 * subdirectory of it (see the {@code subDirectory} overloads); each location is tracked
 * independently. A download is skipped and the cached artifacts reused when a snapshot
 * recorded in {@code lib/bld/.sandbox/sandbox.snapshot} is still valid — validity requires
 * both the resolution inputs (dependencies, repositories, version overrides/boms) and the
 * current on-disk content of the target directory to match what was recorded, so external
 * changes to either force a re-download. Snapshot updates are written atomically.
 * <p>
 * Concurrent downloads for the same extension are serialized; downloads for different
 * extensions may proceed in parallel while still safely sharing the snapshot file.
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.4
 */
@NullMarked
@SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
public final class Sandbox {

    private static final String DEPENDENCIES = "dependencies";
    private static final String REPOSITORIES = "repositories";

    private static final ArtifactRetriever artifactRetriever = ArtifactRetriever.cachingInstance();
    private static final ConcurrentMap<Path, ReentrantLock> directoryLocks = new ConcurrentHashMap<>();
    private static final Logger logger = Logger.getLogger(Sandbox.class.getName());
    private static final ReentrantLock snapshotLock = new ReentrantLock();

    private final String extensionName_;
    private final Path sandboxDirectory_;
    private final Path sandboxExtensionDirectory_;
    private final Path sandboxSnapshotFile_;

    /**
     * Creates a new sandbox manager for the given extension.
     *
     * @param extensionName the unique name of the extension; must be a single, non-blank path segment
     * @param project       the bld project used to resolve {@code lib/bld}; must not be {@code null}
     * @throws NullPointerException     if any argument is {@code null}
     * @throws IllegalArgumentException if {@code extensionName} is blank or is not a single path segment
     *                                  (for example, if it contains a separator, {@code ..} or is absolute)
     */
    public Sandbox(String extensionName, BaseProject project) {
        ObjectTools.requireNonNull(project, "project");
        extensionName_ = TextTools.requireNotBlank(extensionName, "extensionName");
        requireSinglePathSegment(extensionName_);
        sandboxDirectory_ = project.libBldDirectory().toPath().resolve(".sandbox");
        sandboxExtensionDirectory_ = sandboxDirectory_.resolve(extensionName_);
        sandboxSnapshotFile_ = sandboxDirectory_.resolve("sandbox.snapshot");
    }

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

    private static String getDirectoryHash(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return "";
        }
        var manifest = new StringBuilder();
        try (var stream = Files.walk(directory)) {
            var sortedPaths = stream.sorted().toList();
            for (Path path : sortedPaths) {
                manifest.append(directory.relativize(path)).append('|');
                if (Files.isRegularFile(path)) {
                    manifest.append(sha256(path)).append('\n');
                } else {
                    manifest.append("DIR\n");
                }
            }
        }
        return sha256(manifest.toString());
    }

    private static String hashInputs(Collection<Dependency> dependencies,
                                     Collection<Repository> repositories,
                                     VersionResolution resolution) {
        var manifest = new StringBuilder(50);
        for (var dependency : dependencies) {
            manifest.append("dependency|").append(dependency)
                    .append('|').append(dependency.exclusions()).append('\n');
        }
        for (var repository : repositories) {
            manifest.append("repository|").append(repository).append('\n');
        }
        manifest.append("overrides|").append(new TreeMap<>(resolution.versionOverrides()))
                .append("\nboms|").append(new TreeMap<>(resolution.bomVersions())).append('\n');
        return sha256(manifest.toString());
    }

    private static MessageDigest newSha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private static void requireSinglePathSegment(String name) {
        var path = Path.of(name);
        var fileName = path.getFileName();
        if (path.getRoot() != null
                || path.getNameCount() != 1
                || fileName == null
                || ".".equals(fileName.toString())
                || "..".equals(fileName.toString())) {
            throw new IllegalArgumentException("extensionName must be a single path segment: " + name);
        }
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
     * @param dependencies the dependencies to resolve; must not be {@code null}
     * @param repositories the repositories to resolve from; must not be {@code null}
     * @return the path to the extension sandbox directory where artifacts were (or would be) downloaded
     * @throws NullPointerException     if any argument is {@code null} or contain {@code null} elements
     * @throws IllegalArgumentException if any argument is empty or contain empty elements
     * @see #downloadDependencies(List, List, VersionResolution, Path)
     * @see #downloadDependencies(List, List, VersionResolution)
     * @see #downloadDependencies(List, List, Path)
     */
    public Path downloadDependencies(List<Dependency> dependencies, List<Repository> repositories) {
        ObjectTools.requireNotEmpty(dependencies, DEPENDENCIES);
        ObjectTools.requireNotEmpty(repositories, REPOSITORIES);
        return downloadDependencies(dependencies, repositories, new VersionResolution(null), (Path) null);
    }

    /**
     * Downloads the given dependencies using the provided repositories and version resolution.
     * <p>
     * If a valid snapshot already exists, the download is skipped.
     *
     * @param dependencies the dependencies to resolve; must not be {@code null} or empty
     * @param repositories the repositories to resolve from; must not be {@code null} or empty
     * @param resolution   the version resolution strategy; must not be {@code null}
     * @return the path to the extension sandbox directory where artifacts were (or would be) downloaded
     * @throws NullPointerException     if any argument is {@code null} or contain {@code null} elements
     * @throws IllegalArgumentException if any argument is empty or contain empty elements
     * @see #downloadDependencies(List, List, VersionResolution, Path)
     * @see #downloadDependencies(List, List)
     * @see #downloadDependencies(List, List, Path)
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
     * @param dependencies the direct dependencies to resolve; must not be {@code null} or empty
     * @param repositories the repositories to resolve from; must not be {@code null} or empty
     * @param resolution   the version resolution strategy; must not be {@code null}
     * @param subDirectory optional subdirectory within the extension sandbox to place artifacts;
     *                     may be {@code null} to use the extension root
     * @return the path to the directory where artifacts were downloaded
     * @throws NullPointerException     if any argument is {@code null} or contain {@code null} elements
     * @throws IllegalArgumentException if any argument is empty or contain empty elements, or if
     *                                  {@code subDirectory} resolves outside the extension sandbox
     * @throws IllegalStateException    if an existing download directory cannot be deleted
     * @throws UncheckedIOException     if the download directory cannot be created or the snapshot
     *                                  cannot be written
     */
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

        var lock = directoryLocks.computeIfAbsent(
                sandboxExtensionDirectory_.toAbsolutePath().normalize(), k -> new ReentrantLock());
        lock.lock();
        try {
            if (isSnapshotValid(snapshotKey, inputsHash, downloadDirectory)) {
                return downloadDirectory;
            }

            deleteDirectory(downloadDirectory);

            try {
                Files.createDirectories(downloadDirectory);
            } catch (IOException e) {
                throw new UncheckedIOException("Could not create sandbox directory: " + downloadDirectory, e);
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
     * Downloads the given dependencies using the provided repositories and version resolution
     * into an optional subdirectory specified as a {@link File}.
     *
     * @param dependencies the dependencies to resolve; must not be {@code null} or empty
     * @param repositories the repositories to resolve from; must not be {@code null} or empty
     * @param resolution   the version resolution strategy; must not be {@code null}
     * @param subDirectory optional subdirectory as a {@code File}; may be {@code null}
     * @return the path to the directory where artifacts were downloaded
     * @throws NullPointerException     if any argument is {@code null} or contain {@code null} elements
     * @throws IllegalArgumentException if any argument is empty or contain empty elements, or if
     *                                  {@code subDirectory} resolves outside the extension sandbox
     * @throws IllegalStateException    if an existing download directory cannot be deleted
     * @throws UncheckedIOException     if the download directory cannot be created or the snapshot
     *                                  cannot be written
     * @see #downloadDependencies(List, List, VersionResolution, Path)
     * @see #downloadDependencies(List, List, VersionResolution, File)
     * @see #downloadDependencies(List, List, Path)
     * @see #downloadDependencies(List, List, File)
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
     * @param dependencies the direct dependencies to resolve; must not be {@code null} or empty
     * @param repositories the repositories to resolve from; must not be {@code null} or empty
     * @param subDirectory optional subdirectory within the extension sandbox to place artifacts;
     *                     may be {@code null} to use the extension root
     * @return the path to the directory where artifacts were downloaded
     * @throws NullPointerException     if any argument is {@code null} or contain {@code null} elements
     * @throws IllegalArgumentException if any argument is empty or contain empty elements, or if
     *                                  {@code subDirectory} resolves outside the extension sandbox
     * @throws IllegalStateException    if an existing download directory cannot be deleted
     * @throws UncheckedIOException     if the download directory cannot be created or the snapshot
     *                                  cannot be written
     * @see #downloadDependencies(List, List, VersionResolution, Path)
     * @see #downloadDependencies(List, List)
     * @see #downloadDependencies(List, List, VersionResolution)
     */
    public Path downloadDependencies(List<Dependency> dependencies,
                                     List<Repository> repositories,
                                     @Nullable Path subDirectory) {
        ObjectTools.requireNotEmpty(dependencies, DEPENDENCIES);
        ObjectTools.requireNotEmpty(repositories, REPOSITORIES);
        return downloadDependencies(dependencies, repositories, new VersionResolution(null), subDirectory);
    }

    /**
     * Downloads dependencies into the sandbox, optionally under a subdirectory
     * specified as a {@link File}.
     *
     * @param dependencies the dependencies to resolve; must not be {@code null} or empty
     * @param repositories the repositories to resolve from; must not be {@code null} or empty
     * @param subDirectory optional subdirectory as a {@code File}; may be {@code null}
     * @return the path to the directory where artifacts were downloaded
     * @see #downloadDependencies(List, List, Path)
     * @see #downloadDependencies(List, List, VersionResolution, Path)
     * @see #downloadDependencies(List, List, VersionResolution, File)
     * @see #downloadDependencies(List, List)
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
     * Returns the root sandbox directory.
     *
     * @return the path to {@code lib/bld/.sandbox}
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
     * @return the path to {@code lib/bld/.sandbox/<extensionName>}
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

    private boolean isSnapshotValid(String snapshotKey, String inputsHash, Path directory) {
        if (!Files.exists(sandboxSnapshotFile_) || !Files.exists(directory)) {
            return false;
        }
        var snapshot = new Properties();
        try (var in = Files.newInputStream(sandboxSnapshotFile_)) {
            snapshot.load(in);
        } catch (IOException e) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Could not load snapshot, will re-download", e);
            }
            return false;
        }
        var expected = snapshot.getProperty(snapshotKey);
        if (expected == null) {
            return false;
        }
        try {
            return expected.equals(snapshotValue(inputsHash, directory));
        } catch (IOException e) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Could not compute snapshot hash", e);
            }
            return false;
        }
    }

    private @Nullable Path relativeSubDirectory(@Nullable Path subDirectory) {
        if (subDirectory == null) {
            return null;
        }
        var root = sandboxExtensionDirectory_.normalize();
        var resolved = root.resolve(subDirectory).normalize();
        if (!resolved.startsWith(root)) {
            throw new IllegalArgumentException(
                    "subDirectory must be within the extension sandbox: " + subDirectory);
        }
        var relative = root.relativize(resolved);
        return relative.toString().isEmpty() ? null : relative;
    }

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
                    } catch (IOException e) {
                        if (logger.isLoggable(Level.WARNING)) {
                            logger.log(Level.WARNING, "Could not load existing snapshot, will overwrite", e);
                        }
                    }
                }
                snapshot.setProperty(snapshotKey, value);
                writeSnapshot(snapshot);
            } finally {
                snapshotLock.unlock();
            }
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Sandbox snapshots saved.");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save snapshot", e);
        }
    }

    private String snapshotValue(String inputsHash, Path directory) throws IOException {
        return inputsHash + ':' + getDirectoryHash(directory);
    }

    private void writeSnapshot(Properties snapshot) throws IOException {
        var temp = Files.createTempFile(sandboxDirectory_, "sandbox", ".snapshot.tmp");
        try {
            try (var out = Files.newOutputStream(temp)) {
                snapshot.store(out, "Sandbox snapshots");
            }
            try {
                Files.move(temp, sandboxSnapshotFile_, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, sandboxSnapshotFile_, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}