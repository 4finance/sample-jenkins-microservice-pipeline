package io.fourfinanceit.pipeline.example.job

import groovy.io.FileType
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.MemoryJobManagement
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Tests that all dsl scripts in the jobs directory will compile.
 *
 * @author Marcin Grzejszczak
 */
class JobScriptsSpec extends Specification {

    @Unroll
    void 'test script #file.name should compile'() {
        given:
            MemoryJobManagement jm = new MemoryJobManagement()
            jm.parameters << [
                    STASH_HOST: '52.17.120.44:7990',
                    STASH_PROJECT: 'PRs',
                    STASH_REPO: 'test-repo',
                    STASH_USERNAME: 'username',
                    STASH_PASSWORD: 'password',
                    TEST_MODE: true,
                    COUNTRY: "pl",
                    BUILD_PHRASE: "testX"
            ]

        when:
            DslScriptLoader.runDslEngine file.text, jm

        then:
            noExceptionThrown()

        where:
            file << jobFiles
    }

    static List<File> getJobFiles() {
        List<File> files = []
        new File('jobs').eachFileRecurse(FileType.FILES) {
            files << it
        }
        files
    }
}
