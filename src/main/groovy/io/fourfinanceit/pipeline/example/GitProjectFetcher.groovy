package io.fourfinanceit.pipeline.example

import io.fourfinanceit.pipeline.example.offline.OfflineApi
import groovy.json.JsonSlurper

/**
 * @author Marcin Grzejszczak
 * @author Nakul Mishra
 */
class GitProjectFetcher {
    private final boolean testMode
    private final URL reposApi

    GitProjectFetcher(boolean testMode, URL reposApi) {
        this.testMode = testMode
        this.reposApi = reposApi
    }

    def fetchRepos() {
        if (testMode) {
            return new JsonSlurper().parseText(OfflineApi.API)
        }
        return new JsonSlurper().parse(reposApi.newReader())
    }
}
