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
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import rife.bld.BaseProject;
import rife.bld.dependencies.Dependency;
import rife.bld.dependencies.Repository;
import rife.bld.dependencies.VersionNumber;
import rife.bld.dependencies.VersionResolution;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static rife.bld.dependencies.Repository.MAVEN_CENTRAL;

@DisplayName("Sandbox")
class SandboxTest {

    @TempDir
    Path tmp;

    static void deleteDirectory(Path dir) throws Exception {
        var m = Sandbox.class.getDeclaredMethod("deleteDirectory", Path.class);
        m.setAccessible(true);
        m.invoke(null, dir);
    }

    static String hashInputs(List<Dependency> deps, List<Repository> repos, VersionResolution vr) throws Exception {
        var m = Sandbox.class.getDeclaredMethod("hashInputs", Collection.class,
                Collection.class, VersionResolution.class);
        m.setAccessible(true);
        return (String) m.invoke(null, deps, repos, vr);
    }

    @SuppressWarnings("SameParameterValue")
    static boolean isSnapshotValid(Sandbox sandbox, String key, String inputsHash, Path dir) throws Exception {
        var m = Sandbox.class.getDeclaredMethod("isSnapshotValid", String.class, String.class, Path.class);
        m.setAccessible(true);
        return (boolean) m.invoke(sandbox, key, inputsHash, dir);
    }

    static String snapshotValue(Sandbox sandbox, String inputsHash, Path dir) throws Exception {
        var m = Sandbox.class.getDeclaredMethod("snapshotValue", String.class, Path.class);
        m.setAccessible(true);
        return (String) m.invoke(sandbox, inputsHash, dir);
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

        @Test
        void initializesDirectories() throws Exception {
            var proj = project();
            var sandbox = new Sandbox("my-ext", proj);
            assertEquals(tmp.resolve("lib/bld/.sandbox"), sandbox.getSandboxDirectory());
            assertEquals(tmp.resolve("lib/bld/.sandbox/my-ext"), sandbox.getSandboxExtensionDirectory());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "/", "a/b", "../x", "./", ".", "..", "/abs", "a//b"})
        void rejectsInvalidName(String name) throws Exception {
            var proj = project();
            assertThrows(Exception.class, () -> new Sandbox(name, proj));
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void rejectsNullProject() {
            assertThrows(NullPointerException.class, () -> new Sandbox("my-ext", null));
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
            var sandbox = new Sandbox("ext-file-cache", proj);
            var extDir = sandbox.getSandboxExtensionDirectory();
            var subDir = extDir.resolve("mytools");
            Files.createDirectories(subDir);
            Files.writeString(subDir.resolve("dummy.jar"), "content");

            var deps = List.of(new Dependency("org.example", "example", new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);

            String inputsHash = hashInputs(deps, repos, vr);
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
            var sandbox = new Sandbox("ext-path-root", proj);
            var extDir = sandbox.getSandboxExtensionDirectory();
            Files.createDirectories(extDir);
            Files.writeString(extDir.resolve("dummy.jar"), "content");

            var deps = List.of(new Dependency("org.example", "example", new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);

            String inputsHash = hashInputs(deps, repos, vr);
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
            var sandbox = new Sandbox("ext-path-cache", proj);
            var extDir = sandbox.getSandboxExtensionDirectory();
            var subDir = extDir.resolve("tools");
            Files.createDirectories(subDir);
            Files.writeString(subDir.resolve("dummy.jar"), "content");

            var deps = List.of(new Dependency("org.example", "example", new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);

            String inputsHash = hashInputs(deps, repos, vr);
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
            var sandbox = new Sandbox("ext-a", proj);
            var extDir = sandbox.getSandboxExtensionDirectory();
            var subDir = extDir.resolve("sub");
            Files.createDirectories(subDir);
            Files.writeString(subDir.resolve("dummy.jar"), "content");
            var deps = List.of(new Dependency("org.example", "example",
                    new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);
            String inputsHash = hashInputs(deps, repos, vr);
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
            var sandbox = new Sandbox("ext-c", proj);
            var extDir = sandbox.getSandboxExtensionDirectory();
            Files.createDirectories(extDir);
            Files.writeString(extDir.resolve("dummy.jar"), "content");

            var deps = List.of(new Dependency("org.example", "example",
                    new VersionNumber(1, 0)));
            var repos = List.of(MAVEN_CENTRAL);
            var vr = new VersionResolution(null);

            String inputsHash = hashInputs(deps, repos, vr);
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
    @DisplayName("getFastMetadataHash")
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
            Thread.sleep(20);
            Files.writeString(dir.resolve("b.txt"), "world");
            var h3 = (String) hashMethod.invoke(null, dir);
            assertNotEquals(h1, h3);
        }

        @Test
        void returnsEmptyForMissingDir() throws Exception {
            var result = (String) hashMethod.invoke(null, tmp.resolve("nope"));
            assertEquals("", result);
        }

        @BeforeEach
        void setUp() throws Exception {
            hashMethod = Sandbox.class.getDeclaredMethod("getFastMetadataHash", Path.class);
            hashMethod.setAccessible(true);
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
            String inputsHash = hashInputs(deps, repos, vr);
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
            String inputsHash = hashInputs(deps, repos, vr);
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
