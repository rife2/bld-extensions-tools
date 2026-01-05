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

import rife.bld.BuildCommand;
import rife.bld.Project;
import rife.bld.extension.JUnitReporterOperation;
import rife.bld.extension.PmdOperation;
import rife.bld.extension.SpotBugsOperation;
import rife.bld.publish.PublishDeveloper;
import rife.bld.publish.PublishLicense;
import rife.bld.publish.PublishScm;

import java.io.File;
import java.util.List;

import static rife.bld.dependencies.Repository.MAVEN_CENTRAL;
import static rife.bld.dependencies.Repository.RIFE2_RELEASES;
import static rife.bld.dependencies.Scope.test;

public class ToolsBuild extends Project {

    public ToolsBuild() {
        pkg = "rife.bld.extension.tools";
        name = "Extensions Tolls";
        archiveBaseName = "bld-extensions-tools";
        version = version(0, 9, 0, "SNAPSHOT");

        javaRelease = 17;

        downloadSources = true;
        autoDownloadPurge = true;

        repositories = List.of(MAVEN_CENTRAL, RIFE2_RELEASES);

        var junit = version(6, 0, 1);
        scope(test)
                .include(dependency("org.junit.jupiter", "junit-jupiter", junit))
                .include(dependency("org.junit.platform", "junit-platform-console-standalone", junit));

        publishOperation()
                .repository(version.isSnapshot() ?
                        repository("rife2-snapshot") : repository("rife2"))
                .repository(repository("github"))
                .info()
                .groupId("com.uwyn.rife2")
                .artifactId(archiveBaseName)
                .description("Tools for bld Extensions")
                .url("https://github.com/rife2/" + archiveBaseName)
                .developer(new PublishDeveloper()
                        .id("ethauvin")
                        .name("Erik C. Thauvin")
                        .email("erik@thauvin.net")
                        .url("https://erik.thauvin.net/")
                )
                .license(new PublishLicense()
                        .name("The Apache License, Version 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.txt")
                )
                .scm(new PublishScm()
                        .connection("scm:git:https://github.com/rife2/" + archiveBaseName + ".git")
                        .developerConnection("scm:git:git@github.com:rife2/" + archiveBaseName + ".git")
                        .url("https://github.com/rife2/" + archiveBaseName)
                )
                .signKey(property("sign.key"))
                .signPassphrase(property("sign.passphrase"));
    }

    @Override
    public void test() throws Exception {
        var op = testOperation().fromProject(this);
        // Set the reports directory
        op.testToolOptions().reportsDir(new File("build/test-results/test/"));
        op.execute();
    }

    public static void main(String[] args) {
        new ToolsBuild().start(args);
    }

    @BuildCommand(summary = "Runs PMD analysis")
    public void pmd() throws Exception {
        new PmdOperation()
                .fromProject(this)
                .failOnViolation(true)
                .ruleSets("config/pmd.xml")
                .execute();
    }

    @BuildCommand(summary = "Runs the JUnit reporter")
    public void reporter() throws Exception {
        new JUnitReporterOperation()
                .fromProject(this)
                .failOnSummary(true)
                .execute();
    }

    @BuildCommand(summary = "Runs SpotBugs on this project")
    public void spotbugs() throws Exception {
        new SpotBugsOperation()
                .fromProject(this)
                .home("/opt/spotbugs")
                .exclude("config/excludeFilter.xml")
                .execute();
    }
}
