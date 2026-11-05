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

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import rife.bld.BaseProject;
import rife.bld.dependencies.Dependency;
import rife.bld.dependencies.Repository;
import rife.bld.dependencies.VersionNumber;
import rife.bld.dependencies.VersionResolution;
import rife.bld.testing.LoggingExtension;
import rife.bld.testing.TestLogHandler;

import java.io.File;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static rife.bld.dependencies.Repository.CENTRAL_SNAPSHOTS;
import static rife.bld.dependencies.Repository.MAVEN_CENTRAL;

@DisplayName("Sandbox")
class SandboxTest {

    private static final TestLogHandler testLogHandler = new TestLogHandler();

    @RegisterExtension
    @SuppressWarnings({"LoggerInitializedWithForeignClass", "unused"})
    private static final LoggingExtension loggingExtension = new LoggingExtension(
            Logger.getLogger(Sandbox.class.getName()),
            testLogHandler
    );

    @TempDir
    Path tmp;

    static Path anyFile(Path dir) throws Exception {
        try (var walk = Files.walk(dir)) {
            return walk.filter(Files::isRegularFile).findFirst()
                    .orElseThrow(() -> new IllegalStateException("No regular files under " + dir));
        }
    }

    static void deleteDirectory(Path dir) throws Exception {
        var m = Sandbox.class.getDeclaredMethod("deleteDirectory", Path.class);
        m.setAccessible(true);
        m.invoke(null, dir);
    }

    static String hashInputs(Sandbox sandbox,
                             List<Dependency> deps,
                             List<Repository> repos,
                             VersionResolution vr) throws Exception {
        var m = Sandbox.class.getDeclaredMethod("hashInputs", Collection.class,
                Collection.class, VersionResolution.class);
        m.setAccessible(true);
        return (String) m.invoke(sandbox, deps, repos, vr);
    }

    static String hashZipInputs(Sandbox sandbox, URL url, String version) throws Exception {
        var m = Sandbox.class.getDeclaredMethod("hashZipInputs", URL.class, String.class);
        m.setAccessible(true);
        return (String) m.invoke(sandbox, url, version);
    }

    @SuppressWarnings("SameParameterValue")
    static boolean isSnapshotValid(Sandbox sandbox, String key, String inputsHash, Path dir) throws Exception {
        var m = Sandbox.class.getDeclaredMethod("isSnapshotValid", String.class, String.class, Path.class);
        m.setAccessible(true);
        return (boolean) m.invoke(sandbox, key, inputsHash, dir);
    }

    static Properties readSnapshot(Sandbox sandbox) throws Exception {
        var snapFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
        var props = new Properties();
        try (var in = Files.newInputStream(snapFile)) {
            props.load(in);
        }
        return props;
    }

    static String snapshotValue(Sandbox sandbox, String inputsHash, Path dir) throws Exception {
        var m = Sandbox.class.getDeclaredMethod("snapshotValue", String.class, Path.class);
        m.setAccessible(true);
        return (String) m.invoke(sandbox, inputsHash, dir);
    }

    static URL toUrl(String s) {
        try {
            return URI.create(s).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(s, e);
        }
    }

    BaseProject project() throws Exception {
        var workDir = tmp.toFile();
        Files.createDirectories(tmp.resolve("lib/bld"));
        var p = new BaseProject() {
        };
        setWorkDir(p, workDir);
        return p;
    }

    private void setWorkDir(BaseProject p, File dir) throws Exception {
        for (var name : List.of("workDirectory", "workDir", "work_directory")) {
            try {
                Field f = BaseProject.class.getDeclaredField(name);
                f.setAccessible(true);
                f.set(p, dir);
                return;
            } catch (NoSuchFieldException ignore) {
            }
        }
        for (var f : BaseProject.class.getDeclaredFields()) {
            if (File.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                if (f.get(p) == null || f.getName().toLowerCase().contains("work")) {
                    f.set(p, dir);
                    return;
                }
            }
        }
        throw new IllegalStateException("Could not find workDirectory field in BaseProject");
    }

    @Nested
    @DisplayName("constructor")
    class ConstructorTests {

        @ParameterizedTest
        @ValueSource(strings = {"my-ext", "rife.docs", "kotlin_tools", "ext123", "A-B_c.1", "ext-2.0", "foo.bar.baz", "a", "x1"})
        void acceptsValidName(String name) throws Exception {
            var proj = project();
            assertDoesNotThrow(() -> new Sandbox(name, proj, false));
        }

        @Test
        void initializesDirectories() throws Exception {
            var proj = project();
            var sandbox = new Sandbox("my-ext", proj, false);
            assertEquals(tmp.resolve("lib/.sandbox"), sandbox.getSandboxDirectory());
            assertEquals(tmp.resolve("lib/.sandbox/my-ext"), sandbox.getSandboxExtensionDirectory());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                " ", "   ", "/", "a/b", "../x", "./", ".", "..", "/abs", "a//b",
                "a|b", "a\nb", "a\rb",           // manifest-hostile
                "a:b", "a*b", "a?b", "a\"b",     // Windows-hostile
                "a b", "a\tb",                   // whitespace
                "café",                          // non-ASCII
                "---", "...", "___", "-a", "a-",  // punctuation-only or edge punctuation
                ".a", "a.", "_a", "a_",           // leading/trailing punctuation
        })
        void rejectsInvalidName(String name) throws Exception {
            var proj = project();
            assertThrows(Exception.class, () -> new Sandbox(name, proj, false));
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void rejectsNullProject() {
            assertThrows(NullPointerException.class, () -> new Sandbox("my-ext", null, false));
        }
    }

    @Nested
    @DisplayName("deleteDirectory")
    class DeleteTests {

        @Test
        void deletesRecursively() throws Exception {
            var dir = tmp.resolve("toDelete");
            Files.createDirectories(dir.resolve("inner"));
            Files.writeString(dir.resolve("inner/file.txt"), "x");
            deleteDirectory(dir);
            assertFalse(Files.exists(dir));
        }

        @Test
        void noOpIfNotExists() {
            assertDoesNotThrow(() -> deleteDirectory(tmp.resolve("absent")));
        }
    }

    @Nested
    @DisplayName("downloadDependencies - validation & cache path")
    class DownloadTests {

        @Test
        void fileOverloadWithoutResolutionRejectsEscaping() throws Exception {
            var sandbox = new Sandbox("ext-file-no-vr", project());
            var deps = List.of(new Dependency("org.example", "example", new VersionNumber(1, 0)));
            assertThrows(IllegalArgumentException.class, () ->
                    sandbox.downloadDependencies(deps, List.of(MAVEN_CENTRAL), new File("../../escape"))
            );
        }

        @Test
        void fileOverloadWithoutResolutionReturnsTargetDirOnCacheHit() throws Exception {
            var proj = project();
            var sandbox = new Sandbox("ext-file-cache", proj, false);
            var extDir = sandbox.getSandboxExtensionDirectory();
            var subDir = extDir.resolve("mytools");
            Files.createDirectories(subDir);
            Files.writeString(subDir.resolve("dummy.jar"), "content");

            var deps = List.of(new Dependency("org.example", "example", new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);

            String inputsHash = hashInputs(sandbox, deps, repos, vr);
            String snapVal = snapshotValue(sandbox, inputsHash, subDir);

            var snapshotFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapshotFile.getParent());
            var props = new Properties();
            props.setProperty("ext-file-cache/mytools", snapVal);
            try (var out = Files.newOutputStream(snapshotFile)) {
                props.store(out, "test");
            }

            var result = sandbox.downloadDependencies(deps, repos, new File("mytools"));
            assertEquals(subDir, result);
        }


        @Test
        void pathOverloadWithoutResolutionRejectsEscapingSubDirectory() throws Exception {
            var sandbox = new Sandbox("ext-path-no-vr", project());
            var deps = List.of(new Dependency("org.example", "example", new VersionNumber(1, 0)));
            assertThrows(IllegalArgumentException.class, () ->
                    sandbox.downloadDependencies(deps, List.of(MAVEN_CENTRAL), Path.of("../../escape"))
            );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @SuppressWarnings("unchecked")
        void pathOverloadWithoutResolutionRejectsNullOrEmptyDependencies(List<?> invalid) throws Exception {
            var sandbox = new Sandbox("ext-path-no-vr", project());
            assertThrows(Exception.class, () -> sandbox.downloadDependencies(
                    (List<Dependency>) invalid,
                    List.of(MAVEN_CENTRAL),
                    Path.of("tools")
            ));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @SuppressWarnings("unchecked")
        void pathOverloadWithoutResolutionRejectsNullOrEmptyRepositories(List<?> invalid) throws Exception {
            var sandbox = new Sandbox("ext-path-no-vr", project());
            var dep = new Dependency("org.example", "example", new VersionNumber(1, 0));
            assertThrows(Exception.class, () -> sandbox.downloadDependencies(
                    List.of(dep),
                    (List<Repository>) invalid,
                    Path.of("tools")
            ));
        }

        @Test
        void pathOverloadWithoutResolutionReturnsRootOnCacheHit() throws Exception {
            var proj = project();
            var sandbox = new Sandbox("ext-path-root", proj, false);
            var extDir = sandbox.getSandboxExtensionDirectory();
            Files.createDirectories(extDir);
            Files.writeString(extDir.resolve("dummy.jar"), "content");

            var deps = List.of(new Dependency("org.example", "example", new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);

            String inputsHash = hashInputs(sandbox, deps, repos, vr);
            String snapVal = snapshotValue(sandbox, inputsHash, extDir);

            var snapshotFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapshotFile.getParent());
            var props = new Properties();
            props.setProperty("ext-path-root", snapVal);
            try (var out = Files.newOutputStream(snapshotFile)) {
                props.store(out, "test");
            }

            var result = sandbox.downloadDependencies(deps, repos, (Path) null);
            assertEquals(extDir, result);
        }

        @Test
        void pathOverloadWithoutResolutionReturnsTargetDirOnCacheHit() throws Exception {
            var proj = project();
            var sandbox = new Sandbox("ext-path-cache", proj, false);
            var extDir = sandbox.getSandboxExtensionDirectory();
            var subDir = extDir.resolve("tools");
            Files.createDirectories(subDir);
            Files.writeString(subDir.resolve("dummy.jar"), "content");

            var deps = List.of(new Dependency("org.example", "example", new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);

            String inputsHash = hashInputs(sandbox, deps, repos, vr);
            String snapVal = snapshotValue(sandbox, inputsHash, subDir);

            var snapshotFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapshotFile.getParent());
            var props = new Properties();
            props.setProperty("ext-path-cache/tools", snapVal);
            try (var out = Files.newOutputStream(snapshotFile)) {
                props.store(out, "test");
            }

            var result = sandbox.downloadDependencies(deps, repos, Path.of("tools"));
            assertEquals(subDir, result);
            assertTrue(Files.exists(subDir.resolve("dummy.jar")));
        }

        @Test
        void rejectsEscapingSubDirectory() throws Exception {
            var sandbox = new Sandbox("ext-a", project());
            var deps = List.of(new Dependency("org.example", "example", new VersionNumber(1, 0)));
            assertThrows(IllegalArgumentException.class, () ->
                    sandbox.downloadDependencies(deps, List.of(MAVEN_CENTRAL), new VersionResolution(null), Path.of("../../escape"))
            );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @SuppressWarnings("unchecked")
        void rejectsNullOrEmptyDependencies(List<?> invalid) throws Exception {
            var sandbox = new Sandbox("ext-b", project());
            assertThrows(Exception.class, () -> sandbox.downloadDependencies(
                    (List<Dependency>) invalid,
                    List.of(MAVEN_CENTRAL)
            ));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @SuppressWarnings("unchecked")
        void rejectsNullOrEmptyRepositories(List<?> invalid) throws Exception {
            var sandbox = new Sandbox("ext-b", project());
            var dep = new Dependency("org.example", "example", new VersionNumber(1, 0));
            assertThrows(Exception.class, () -> sandbox.downloadDependencies(
                    List.of(dep),
                    (List<Repository>) invalid
            ));
        }

        @Test
        void returnsTargetDirOnCacheHit() throws Exception {
            var proj = project();
            var sandbox = new Sandbox("ext-a", proj, false);
            var extDir = sandbox.getSandboxExtensionDirectory();
            var subDir = extDir.resolve("sub");
            Files.createDirectories(subDir);
            Files.writeString(subDir.resolve("dummy.jar"), "content");
            var deps = List.of(new Dependency("org.example", "example",
                    new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);
            String inputsHash = hashInputs(sandbox, deps, repos, vr);
            String snapVal = snapshotValue(sandbox, inputsHash, subDir);
            var snapshotFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapshotFile.getParent());
            var props = new Properties();
            props.setProperty("ext-a/sub", snapVal);
            try (var out = Files.newOutputStream(snapshotFile)) {
                props.store(out, "test");
            }
            var result = sandbox.downloadDependencies(deps, repos, vr, Path.of("sub"));
            assertEquals(subDir, result);
            assertTrue(Files.exists(subDir.resolve("dummy.jar")));
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void threeArgOverloadRejectsNullResolution() throws Exception {
            var sandbox = new Sandbox("ext-b", project());
            var dep = new Dependency("org.example", "example", new VersionNumber(1, 0));
            assertThrows(Exception.class, () -> sandbox.downloadDependencies(
                    List.of(dep),
                    List.of(MAVEN_CENTRAL),
                    (VersionResolution) null
            ));
        }

        @Test
        void threeArgOverloadReturnsTargetDirOnCacheHit() throws Exception {
            var proj = project();
            var sandbox = new Sandbox("ext-c", proj, false);
            var extDir = sandbox.getSandboxExtensionDirectory();
            Files.createDirectories(extDir);
            Files.writeString(extDir.resolve("dummy.jar"), "content");

            var deps = List.of(new Dependency("org.example", "example",
                    new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);

            String inputsHash = hashInputs(sandbox, deps, repos, vr);
            String snapVal = snapshotValue(sandbox, inputsHash, extDir);

            var snapshotFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapshotFile.getParent());
            var props = new Properties();
            props.setProperty("ext-c", snapVal);
            try (var out = Files.newOutputStream(snapshotFile)) {
                props.store(out, "test");
            }

            var result = sandbox.downloadDependencies(deps, repos, vr);
            assertEquals(extDir, result);
            assertTrue(Files.exists(extDir.resolve("dummy.jar")));
        }
    }

    @Nested
    @DisplayName("downloadZip - hash & cache path")
    class DownloadZipCacheTests {

        @Test
        void createsGitIgnoreAfterDownload() throws Exception {
            var zip = tmp.resolve("simple.zip");
            try (var zos = new ZipOutputStream(Files.newOutputStream(zip))) {
                zos.putNextEntry(new ZipEntry("hello.txt"));
                zos.write("hello".getBytes());
                zos.closeEntry();
            }
            var proj = project();
            var sandbox = new Sandbox("zip-gitignore", proj, false);
            sandbox.downloadZip(zip.toUri().toURL(), "v1");

            var gitignore = tmp.resolve("lib/.gitignore");
            assertTrue(Files.exists(gitignore), "expected .gitignore to be created");
            assertTrue(Files.readString(gitignore).contains(".sandbox/"));
        }

        @Test
        void extensionNameIsPartOfZipHash() throws Exception {
            var url = toUrl("https://example.com/x.zip");
            var s1 = new Sandbox("ext-zip-one", project());
            var s2 = new Sandbox("ext-zip-two", project());
            assertNotEquals(
                    hashZipInputs(s1, url, "v1"),
                    hashZipInputs(s2, url, "v1"));
        }

        @Test
        void fileOverloadReturnsTargetDirOnCacheHit() throws Exception {
            var proj = project();
            var sandbox = new Sandbox("zip-file-cache", proj, false);
            var subDir = sandbox.getSandboxExtensionDirectory().resolve("jbang");
            Files.createDirectories(subDir);
            Files.writeString(subDir.resolve("x.txt"), "content");

            var url = toUrl("https://example.com/x.zip");
            var version = "v1";
            var inputsHash = hashZipInputs(sandbox, url, version);
            var snapVal = snapshotValue(sandbox, inputsHash, subDir);

            var snapFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapFile.getParent());
            var props = new Properties();
            props.setProperty("zip-file-cache/jbang", snapVal);
            try (var out = Files.newOutputStream(snapFile)) {
                props.store(out, "test");
            }

            var result = sandbox.downloadZip(url, version, new File("jbang"));
            assertEquals(subDir, result);
        }

        @Test
        void returnsSubDirectoryOnCacheHit() throws Exception {
            var proj = project();
            var sandbox = new Sandbox("zip-sub-cache", proj, false);
            var subDir = sandbox.getSandboxExtensionDirectory().resolve("tools");
            Files.createDirectories(subDir);
            Files.writeString(subDir.resolve("a.txt"), "content");

            var url = toUrl("https://example.com/dummy.zip");
            var version = "1.0";
            var inputsHash = hashZipInputs(sandbox, url, version);
            var snapVal = snapshotValue(sandbox, inputsHash, subDir);

            var snapFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapFile.getParent());
            var props = new Properties();
            props.setProperty("zip-sub-cache/tools", snapVal);
            try (var out = Files.newOutputStream(snapFile)) {
                props.store(out, "test");
            }

            var result = sandbox.downloadZip(url, version, Path.of("tools"));
            assertEquals(subDir, result);
        }

        @Test
        void returnsTargetDirOnCacheHit() throws Exception {
            var proj = project();
            var sandbox = new Sandbox("zip-cache", proj, false);
            var extDir = sandbox.getSandboxExtensionDirectory();
            Files.createDirectories(extDir);
            Files.writeString(extDir.resolve("extracted.txt"), "content");

            var url = toUrl("https://example.com/dummy.zip");
            var version = "1.0";
            var inputsHash = hashZipInputs(sandbox, url, version);
            var snapVal = snapshotValue(sandbox, inputsHash, extDir);

            var snapFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapFile.getParent());
            var props = new Properties();
            props.setProperty("zip-cache", snapVal);
            try (var out = Files.newOutputStream(snapFile)) {
                props.store(out, "test");
            }

            // Must return without any network access.
            var result = sandbox.downloadZip(url, version);
            assertEquals(extDir, result);
            assertTrue(Files.exists(extDir.resolve("extracted.txt")));
        }

        @Test
        void urlIsPartOfHash() throws Exception {
            var sandbox = new Sandbox("zip-hash", project());
            assertNotEquals(
                    hashZipInputs(sandbox, toUrl("https://example.com/a.zip"), "v1"),
                    hashZipInputs(sandbox, toUrl("https://example.com/b.zip"), "v1"));
        }

        @Test
        void versionIsPartOfHash() throws Exception {
            var sandbox = new Sandbox("zip-hash", project());
            var url = toUrl("https://example.com/x.zip");
            assertNotEquals(
                    hashZipInputs(sandbox, url, "v1"),
                    hashZipInputs(sandbox, url, "v2"));
        }
    }

    @Nested
    @DisplayName("downloadZip - live")
    @Tag("live")
    class DownloadZipLiveTests {

        static final String BLD_ZIP =
                "https://github.com/rife2/bld/releases/download/3.0.1/bld-3.0.1.zip";
        static final String JBANG_ZIP =
                "https://github.com/jbangdev/jbang/releases/download/v0.143.0/jbang.zip";

        @Test
        void cacheHitDoesNotTouchFiles() throws Exception {
            var sandbox = new Sandbox("zip-live-cache", project());
            var url = toUrl(BLD_ZIP);

            var dir1 = sandbox.downloadZip(url, "3.0.1");
            var file = anyFile(dir1);
            var oldTime = FileTime.fromMillis(1234567890000L);
            Files.setLastModifiedTime(file, oldTime);

            // Second call with the same version must be served from the snapshot,
            // so nothing should be re-extracted (mtime preserved).
            var dir2 = sandbox.downloadZip(url, "3.0.1");
            assertEquals(dir1, dir2);
            assertEquals(oldTime, Files.getLastModifiedTime(file),
                    "cache hit must not re-extract files");
        }

        @Test
        void downloadBldSilent() throws Exception {
            var sandbox = new Sandbox("zip-live-email-silent", project(), true);
            sandbox.downloadZip(toUrl("https://github.com/apache/commons-email/archive/refs/tags/EMAIL_1_0.zip"),
                    "1.0");
            testLogHandler.printLogMessages();
            assertTrue(testLogHandler.isEmpty());
        }

        @Test
        void downloadsAndExtractsJbangToSubDirectory() throws Exception {
            var sandbox = new Sandbox("zip-live-jbang", project());
            var sub = Path.of("jbang");
            var result = sandbox.downloadZip(toUrl(JBANG_ZIP), "0.143.0", sub);

            assertEquals(sandbox.getSandboxExtensionDirectory().resolve(sub), result);
            assertTrue(Files.isDirectory(result));
            assertTrue(Files.exists(anyFile(result)));

            var props = readSnapshot(sandbox);
            assertTrue(props.containsKey("zip-live-jbang/jbang"));
        }

        @Test
        void downloadsAndExtractsRifeBld() throws Exception {
            var sandbox = new Sandbox("zip-live-bld", project());
            var result = sandbox.downloadZip(toUrl(BLD_ZIP), "3.0.1");

            assertEquals(sandbox.getSandboxExtensionDirectory(), result);
            assertTrue(Files.isDirectory(result));
            assertTrue(Files.exists(anyFile(result)));

            var snapFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            assertTrue(Files.exists(snapFile));
            var props = readSnapshot(sandbox);
            assertTrue(props.containsKey("zip-live-bld"));
            assertTrue(props.getProperty("zip-live-bld").contains(":"));
        }

        @Test
        void fileOverloadDownloads() throws Exception {
            var sandbox = new Sandbox("zip-live-file", project());
            var result = sandbox.downloadZip(
                    toUrl(JBANG_ZIP), "0.143.0", new File("jbang-file"));

            assertEquals(sandbox.getSandboxExtensionDirectory().resolve("jbang-file"), result);
            assertTrue(Files.isDirectory(result));
            assertTrue(Files.exists(anyFile(result)));
        }

        @Test
        void recoversFromDirectoryDeletion() throws Exception {
            var sandbox = new Sandbox("zip-live-recover", project());
            var url = toUrl(BLD_ZIP);

            var dir = sandbox.downloadZip(url, "3.0.1");
            assertTrue(Files.isDirectory(dir));

            deleteDirectory(dir);
            assertFalse(Files.exists(dir));

            var restored = sandbox.downloadZip(url, "3.0.1");
            assertTrue(Files.isDirectory(restored));
            assertTrue(Files.exists(anyFile(restored)));
        }

        @Test
        void versionChangeInvalidatesCache() throws Exception {
            var sandbox = new Sandbox("zip-live-version", project());
            var url = toUrl(BLD_ZIP);

            var dir1 = sandbox.downloadZip(url, "3.0.1");
            var snap1 = readSnapshot(sandbox).getProperty("zip-live-version");

            // Same URL, different version: inputs hash changes, so the snapshot must
            // be invalidated and the archive re-extracted.
            var dir2 = sandbox.downloadZip(url, "3.0.1-other");
            assertEquals(dir1, dir2);
            var snap2 = readSnapshot(sandbox).getProperty("zip-live-version");

            assertNotNull(snap1);
            assertNotNull(snap2);
            assertNotEquals(snap1, snap2,
                    "changing the version must invalidate the snapshot");
        }
    }

    @Nested
    @DisplayName("downloadZip - security")
    class DownloadZipSecurityTests {

        @Test
        void extractsNestedDirectories() throws Exception {
            var zip = tmp.resolve("nested.zip");
            try (var zos = new ZipOutputStream(Files.newOutputStream(zip))) {
                zos.putNextEntry(new ZipEntry("a/"));
                zos.closeEntry();
                zos.putNextEntry(new ZipEntry("a/b/"));
                zos.closeEntry();
                zos.putNextEntry(new ZipEntry("a/b/c.txt"));
                zos.write("c".getBytes());
                zos.closeEntry();
            }

            var sandbox = new Sandbox("zip-nested", project());
            var result = sandbox.downloadZip(zip.toUri().toURL(), "v1");
            assertEquals("c", Files.readString(result.resolve("a/b/c.txt")));
        }

        @Test
        void rejectsZipSlipEntry() throws Exception {
            var zip = tmp.resolve("evil.zip");
            try (var zos = new ZipOutputStream(Files.newOutputStream(zip))) {
                zos.putNextEntry(new ZipEntry("ok.txt"));
                zos.write("ok".getBytes());
                zos.closeEntry();
                zos.putNextEntry(new ZipEntry("../evil.txt"));
                zos.write("evil".getBytes());
                zos.closeEntry();
            }

            var sandbox = new Sandbox("zip-slip", project());
            var ex = assertThrows(UncheckedIOException.class,
                    () -> sandbox.downloadZip(zip.toUri().toURL(), "v1"));
            assertNotNull(ex.getCause());
            assertTrue(ex.getCause().getMessage().contains("escapes"));

            // The escaping file must not have been written.
            assertFalse(Files.exists(sandbox.getSandboxDirectory().resolve("evil.txt")));
        }
    }

    @Nested
    @DisplayName("downloadZip - validation")
    class DownloadZipValidationTests {

        @Test
        void rejectsEscapingFileSubDirectory() throws Exception {
            var sandbox = new Sandbox("zip-escape-file", project());
            var url = toUrl("https://example.com/x.zip");
            assertThrows(IllegalArgumentException.class,
                    () -> sandbox.downloadZip(url, "v1", new File("../../escape")));
        }

        @Test
        void rejectsEscapingPathSubDirectory() throws Exception {
            var sandbox = new Sandbox("zip-escape", project());
            var url = toUrl("https://example.com/x.zip");
            assertThrows(IllegalArgumentException.class,
                    () -> sandbox.downloadZip(url, "v1", Path.of("../../escape")));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        void rejectsNullOrBlankVersion(String version) throws Exception {
            var sandbox = new Sandbox("zip-bad-version", project());
            var url = toUrl("https://example.com/x.zip");
            assertThrows(Exception.class, () -> sandbox.downloadZip(url, version));
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void rejectsNullUrl() throws Exception {
            var sandbox = new Sandbox("zip-null-url", project());
            assertThrows(NullPointerException.class, () -> sandbox.downloadZip(null, "v1"));
        }

        @Test
        void rejectsVersionWithColon() throws Exception {
            var sandbox = new Sandbox("zip-colon", project());
            var url = toUrl("https://example.com/x.zip");
            assertThrows(IllegalArgumentException.class, () -> sandbox.downloadZip(url, "v1:beta"));
        }
    }

    @Nested
    @DisplayName("getters")
    class GettersTests {

        @Test
        void fileAndPathGettersAreConsistent() throws Exception {
            var sandbox = new Sandbox("ext-get", project());
            assertEquals(sandbox.getSandboxDirectory().toFile(), sandbox.getSandboxDirectoryAsFile());
            assertEquals(sandbox.getSandboxExtensionDirectory().toFile(), sandbox.getSandboxExtensionDirectoryAsFile());
        }
    }

    @Nested
    @DisplayName("hashInputs canonicalization")
    class HashInputsCanonicalizationTests {

        @Test
        void dependencyOrderDoesNotAffectHash() throws Exception {
            var sandbox = new Sandbox("hash-order", project());
            var vr = new VersionResolution(null);
            var repos = List.of(MAVEN_CENTRAL);

            var a = new Dependency("org.example", "aaa", new VersionNumber(1, 0));
            var b = new Dependency("org.example", "bbb", new VersionNumber(2, 0));

            String forward = hashInputs(sandbox, List.of(a, b), repos, vr);
            String reverse = hashInputs(sandbox, List.of(b, a), repos, vr);

            assertEquals(forward, reverse);
        }

        @Test
        void differentDependenciesProduceDifferentHash() throws Exception {
            var sandbox = new Sandbox("hash-diff", project());
            var vr = new VersionResolution(null);
            var repos = List.of(MAVEN_CENTRAL);

            String h1 = hashInputs(sandbox,
                    List.of(new Dependency("org.example", "aaa", new VersionNumber(1, 0))), repos, vr);
            String h2 = hashInputs(sandbox,
                    List.of(new Dependency("org.example", "aaa", new VersionNumber(2, 0))), repos, vr);

            assertNotEquals(h1, h2);
        }

        @Test
        void extensionNameIsPartOfHash() throws Exception {
            var vr = new VersionResolution(null);
            var repos = List.of(MAVEN_CENTRAL);
            var deps = List.of(new Dependency("org.example", "aaa", new VersionNumber(1, 0)));

            var s1 = new Sandbox("ext-one", project());
            var s2 = new Sandbox("ext-two", project());

            String h1 = hashInputs(s1, deps, repos, vr);
            String h2 = hashInputs(s2, deps, repos, vr);

            assertNotEquals(h1, h2,
                    "inputs hash must incorporate the extension name");
        }

        @Test
        void repositoryOrderDoesNotAffectHash() throws Exception {
            var sandbox = new Sandbox("hash-order-repos", project());
            var vr = new VersionResolution(null);
            var deps = List.of(new Dependency("org.example", "aaa", new VersionNumber(1, 0)));

            var r1 = MAVEN_CENTRAL;
            var r2 = CENTRAL_SNAPSHOTS;

            String forward = hashInputs(sandbox, deps, List.of(r1, r2), vr);
            String reverse = hashInputs(sandbox, deps, List.of(r2, r1), vr);

            assertEquals(forward, reverse);
        }
    }

    @Nested
    @DisplayName("getDirectoryHash")
    class HashTests {

        Method hashMethod;

        @Test
        void hashChangesWhenContentChanges() throws Exception {
            var dir = tmp.resolve("data");
            Files.createDirectories(dir);
            Files.writeString(dir.resolve("a.txt"), "hello");
            var h1 = (String) hashMethod.invoke(null, dir);
            var h2 = (String) hashMethod.invoke(null, dir);
            assertEquals(h1, h2);
            Files.writeString(dir.resolve("b.txt"), "world");
            var h3 = (String) hashMethod.invoke(null, dir);
            assertNotEquals(h1, h3);
        }

        @Test
        void hashIgnoresMtimeButDetectsSameSizeEdit() throws Exception {
            var dir = tmp.resolve("data");
            Files.createDirectories(dir);
            var file = dir.resolve("a.txt");
            Files.writeString(file, "hello");
            var h1 = (String) hashMethod.invoke(null, dir);

            Files.setLastModifiedTime(file, FileTime.fromMillis(12345));
            assertEquals(h1, hashMethod.invoke(null, dir));

            Files.writeString(file, "jello");
            assertNotEquals(h1, hashMethod.invoke(null, dir));
        }

        @Test
        void returnsEmptyForMissingDir() throws Exception {
            var result = (String) hashMethod.invoke(null, tmp.resolve("nope"));
            assertEquals("", result);
        }

        @BeforeEach
        void setUp() throws Exception {
            hashMethod = Sandbox.class.getDeclaredMethod("getDirectoryHash", Path.class);
            hashMethod.setAccessible(true);
        }
    }

    @Nested
    @DisplayName("JVM version invalidation")
    @Execution(ExecutionMode.SAME_THREAD) // static supplier in Sandbox; must not run concurrently
    class JvmVersionTests {

        @Test
        void differentJvmVersionInvalidatesSnapshot() throws Exception {
            var sandbox = new Sandbox("jvm-ext", project());
            var extDir = sandbox.getSandboxExtensionDirectory();
            Files.createDirectories(extDir);
            Files.writeString(extDir.resolve("f.txt"), "data");

            var deps = List.of(new Dependency("org.example", "example", new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);
            String inputsHash = hashInputs(sandbox, deps, repos, vr);

            Sandbox.setJvmVersionSupplier(() -> "21.0.5");
            String snapVal = snapshotValue(sandbox, inputsHash, extDir);
            var snapFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapFile.getParent());
            var props = new Properties();
            props.setProperty("jvm-ext", snapVal);
            try (var out = Files.newOutputStream(snapFile)) {
                props.store(out, "test");
            }

            assertTrue(isSnapshotValid(sandbox, "jvm-ext", inputsHash, extDir));

            Sandbox.setJvmVersionSupplier(() -> "22.0.1");
            assertFalse(isSnapshotValid(sandbox, "jvm-ext", inputsHash, extDir),
                    "a JVM version change must invalidate the snapshot");
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void nullFromSupplierIsRejected() throws Exception {
            var sandbox = new Sandbox("jvm-null", project());
            Sandbox.setJvmVersionSupplier(() -> null);
            assertThrows(NullPointerException.class, sandbox::jvmVersion);
        }

        @AfterEach
        void resetSupplier() {
            Sandbox.setJvmVersionSupplier(null);
        }

        @Test
        void sameJvmVersionRemainsValid() throws Exception {
            var sandbox = new Sandbox("jvm-ext-same", project());
            var extDir = sandbox.getSandboxExtensionDirectory();
            Files.createDirectories(extDir);
            Files.writeString(extDir.resolve("f.txt"), "data");

            var deps = List.of(new Dependency("org.example", "example", new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);
            String inputsHash = hashInputs(sandbox, deps, repos, vr);

            Sandbox.setJvmVersionSupplier(() -> "21.0.5");
            String snapVal = snapshotValue(sandbox, inputsHash, extDir);
            var snapFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapFile.getParent());
            var props = new Properties();
            props.setProperty("jvm-ext-same", snapVal);
            try (var out = Files.newOutputStream(snapFile)) {
                props.store(out, "test");
            }

            assertTrue(isSnapshotValid(sandbox, "jvm-ext-same", inputsHash, extDir));
        }

        @Test
        void snapshotValueEmbeddedJvmVersionIsPartOfFingerprint() throws Exception {
            var sandbox = new Sandbox("jvm-ext-fp", project());
            var extDir = sandbox.getSandboxExtensionDirectory();
            Files.createDirectories(extDir);
            Files.writeString(extDir.resolve("f.txt"), "data");

            Sandbox.setJvmVersionSupplier(() -> "21.0.5");
            String v21 = snapshotValue(sandbox, "inputs", extDir);

            Sandbox.setJvmVersionSupplier(() -> "22.0.1");
            String v22 = snapshotValue(sandbox, "inputs", extDir);

            assertNotEquals(v21, v22);
            assertTrue(v21.contains("21.0.5"));
            assertTrue(v22.contains("22.0.1"));
        }
    }

    @Nested
    @DisplayName("live downloadDependencies")
    @Tag("live")
    class LiveTests {

        @Test
        void changingDependencyInvalidatesCache() throws Exception {
            var sandbox = new Sandbox("live-ext-invalidate", project());
            var dir1 = sandbox.downloadDependencies(List.of(simple()), List.of(MAVEN_CENTRAL));
            var snapFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            var props1 = new Properties();
            try (var in = Files.newInputStream(snapFile)) {
                props1.load(in);
            }
            var hash1 = props1.getProperty("live-ext-invalidate");
            try (var walk = Files.walk(dir1)) {
                assertTrue(walk.anyMatch(p -> p.toString().contains("slf4j-api")));
            }

            var dir2 = sandbox.downloadDependencies(List.of(withTransitives()), List.of(MAVEN_CENTRAL));
            var props2 = new Properties();
            try (var in = Files.newInputStream(snapFile)) {
                props2.load(in);
            }
            var hash2 = props2.getProperty("live-ext-invalidate");

            assertNotEquals(hash1, hash2);
            assertEquals(dir1, dir2);
            try (var walk = Files.walk(dir2)) {
                assertFalse(walk.anyMatch(p -> p.toString().contains("slf4j-api")),
                        "clean state should remove previous jars");
            }
            try (var walk = Files.walk(dir2)) {
                assertTrue(walk.anyMatch(p -> p.toString().contains("jackson-databind")));
            }
        }

        @Test
        void downloadJunitSilent() throws Exception {
            var sandbox = new Sandbox("live-junit-silent", project(), true);
            sandbox.downloadDependencies(
                    List.of(new Dependency("junit", "junit",
                            new VersionNumber(4, 13, 2))),
                    List.of(MAVEN_CENTRAL),
                    new VersionResolution(null),
                    new File("junit")
            );
            assertTrue(testLogHandler.isEmpty());
        }

        @Test
        void downloadOlderVersion() throws Exception {
            var sandbox = new Sandbox("live-old-version", project());
            var result = sandbox.downloadDependencies(
                    List.of(new Dependency("org.jetbrains.dokka", "dokka-cli",
                            new VersionNumber(2, 2, 0))),
                    List.of(MAVEN_CENTRAL),
                    new VersionResolution(null),
                    new File("mytools")
            );
            assertTrue(Files.exists(result));
            assertEquals(sandbox.getSandboxExtensionDirectory().resolve("mytools"), result);

            try (var walk = Files.walk(result)) {
                assertTrue(walk.anyMatch(p -> p.toString().endsWith("dokka-cli-2.2.0.jar")));
            }
        }

        @Test
        void downloadsArtifactWithTransitiveDependencies() throws Exception {
            var sandbox = new Sandbox("live-transitive", project());
            var result = sandbox.downloadDependencies(
                    List.of(withTransitives()),
                    List.of(MAVEN_CENTRAL),
                    new VersionResolution(null),
                    (Path) null
            );
            assertTrue(Files.exists(result));
            List<Path> jars;
            try (var walk = Files.walk(result)) {
                jars = walk.filter(p -> p.toString().endsWith(".jar")).toList();
            }
            assertTrue(jars.size() >= 3, "expected at least 3 jars, got: " + jars);
            assertTrue(jars.stream().anyMatch(p -> p.getFileName().toString().contains("jackson-databind")));
            assertTrue(jars.stream().anyMatch(p -> p.getFileName().toString().contains("jackson-core")));
            assertTrue(jars.stream().anyMatch(p -> p.getFileName().toString().contains("jackson-annotations")));
            var snapFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            assertTrue(Files.exists(snapFile));
            var props = new Properties();
            try (var in = Files.newInputStream(snapFile)) {
                props.load(in);
            }
            assertTrue(props.containsKey("live-transitive"));
            assertTrue(props.getProperty("live-transitive").contains(":"));
        }

        @Test
        void downloadsToSubDirectoryAndCaches() throws Exception {
            var sandbox = new Sandbox("live-ext-sub", project());
            var deps = List.of(withTransitives());
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);
            var tools = Path.of("tools");
            var first = sandbox.downloadDependencies(deps, repos, vr, tools);
            assertTrue(Files.exists(first));
            try (var walk = Files.walk(first)) {
                assertTrue(walk.anyMatch(p -> p.toString().contains("jackson-databind")));
            }
            var second = sandbox.downloadDependencies(deps, repos, vr, tools);
            assertEquals(first, second);
            assertTrue(Files.exists(second));
        }

        @Test
        void fileOverloadDelegatesToPath() throws Exception {
            var sandbox = new Sandbox("live-ext-file", project());
            var result = sandbox.downloadDependencies(
                    List.of(withTransitives()),
                    List.of(MAVEN_CENTRAL),
                    new VersionResolution(null),
                    new File("mytools")
            );
            assertTrue(Files.exists(result));
            assertEquals(sandbox.getSandboxExtensionDirectory().resolve("mytools"), result);
            try (var walk = Files.walk(result)) {
                assertTrue(walk.anyMatch(p -> p.toString().endsWith("jackson-core-2.15.2.jar")));
            }
        }

        @Test
        void fileOverloadWithoutResolutionDownloads() throws Exception {
            var sandbox = new Sandbox("live-ext-file-no-vr", project());
            var result = sandbox.downloadDependencies(
                    List.of(withTransitives()),
                    List.of(MAVEN_CENTRAL),
                    new File("mytools-no-vr")
            );
            assertTrue(Files.exists(result));
            assertEquals(sandbox.getSandboxExtensionDirectory().resolve("mytools-no-vr"), result);
            try (var walk = Files.walk(result)) {
                assertTrue(walk.anyMatch(p -> p.toString().contains("jackson-databind")));
            }
        }

        @Test
        void modulesDirectoryCreated() throws Exception {
            var sandbox = new Sandbox("live-modules", project());
            var result = sandbox.downloadDependencies(List.of(withTransitives()), List.of(MAVEN_CENTRAL));
            assertTrue(Files.exists(result));
            try (var walk = Files.walk(result)) {
                assertTrue(walk.anyMatch(p -> p.getFileName().toString().endsWith(".jar")));
            }
        }

        @Test
        void pathOverloadWithoutResolutionDownloadsToRoot() throws Exception {
            var sandbox = new Sandbox("live-ext-path-root-no-vr", project());
            var result = sandbox.downloadDependencies(List.of(withTransitives()), List.of(MAVEN_CENTRAL), (Path) null);
            assertTrue(Files.exists(result));
            try (var walk = Files.walk(result)) {
                assertTrue(walk.anyMatch(p -> p.toString().endsWith(".jar")));
            }
        }

        @Test
        void pathOverloadWithoutResolutionDownloadsToSubDirectoryAndCaches() throws Exception {
            var sandbox = new Sandbox("live-ext-path-no-vr", project());
            var deps = List.of(withTransitives());
            var repos = List.of(MAVEN_CENTRAL);
            var tools = Path.of("tools-no-vr");
            var first = sandbox.downloadDependencies(deps, repos, tools);
            assertTrue(Files.exists(first));
            try (var walk = Files.walk(first)) {
                assertTrue(walk.anyMatch(p -> p.toString().contains("jackson-databind")));
            }
            var second = sandbox.downloadDependencies(deps, repos, tools);
            assertEquals(first, second);
        }

        Dependency simple() {
            return new Dependency("org.slf4j", "slf4j-api",
                    new VersionNumber(1, 7, 36));
        }

        Dependency withTransitives() {
            return new Dependency("com.fasterxml.jackson.core", "jackson-databind",
                    new VersionNumber(2, 15, 2));
        }
    }

    @Nested
    @DisplayName("snapshot validity")
    class SnapshotTests {

        @Test
        void invalidWhenFilesMissing() throws Exception {
            var sandbox = new Sandbox("ext-snap", project());
            assertFalse(isSnapshotValid(sandbox, "ext-snap", "any", tmp.resolve("no-such-dir")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"other-ext", "wrong-hash"})
        void invalidWhenMismatch(String scenario) throws Exception {
            var sandbox = new Sandbox("ext-snap", project());
            var extDir = sandbox.getSandboxExtensionDirectory();
            Files.createDirectories(extDir);
            Files.writeString(extDir.resolve("f.txt"), "data");
            var deps = List.of(new Dependency("org.example", "example",
                    new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);
            String inputsHash = hashInputs(sandbox, deps, repos, vr);
            var snapFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapFile.getParent());
            var props = new Properties();
            if (scenario.equals("other-ext")) {
                props.setProperty("other-ext", snapshotValue(sandbox, inputsHash, extDir));
            } else {
                props.setProperty("ext-snap", "definitely-wrong");
            }
            try (var out = Files.newOutputStream(snapFile)) {
                props.store(out, "");
            }
            assertFalse(isSnapshotValid(sandbox, "ext-snap", inputsHash, extDir));
        }

        @Test
        void validWhenHashMatches() throws Exception {
            var sandbox = new Sandbox("ext-snap", project());
            var extDir = sandbox.getSandboxExtensionDirectory();
            Files.createDirectories(extDir);
            Files.writeString(extDir.resolve("f.txt"), "data");
            var deps = List.of(new Dependency("org.example", "example",
                    new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);
            String inputsHash = hashInputs(sandbox, deps, repos, vr);
            String snapVal = snapshotValue(sandbox, inputsHash, extDir);
            var snapFile = sandbox.getSandboxDirectory().resolve("sandbox.snapshot");
            Files.createDirectories(snapFile.getParent());
            var props = new Properties();
            props.setProperty("ext-snap", snapVal);
            try (var out = Files.newOutputStream(snapFile)) {
                props.store(out, "");
            }
            assertTrue(isSnapshotValid(sandbox, "ext-snap", inputsHash, extDir));
        }
    }
}