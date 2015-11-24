package io.fourfinanceit.pipeline.example.scm

import io.fourfinanceit.pipeline.example.Nameable

/**
 * @author Marcin Zajączkowski
 * @author Marcin Grzejszczak
 * @author Nakul Mishra
 */
class GitHubConfig extends AbstractGitConfig {

    private static final String GITHUB_BASE_REPO_URL = "https://github.com"

    private GitHubConfig(Nameable project, String group, URI baseRepoUrl, String credentialsId) {
        super(project, group, baseRepoUrl, credentialsId)
    }

    static GitHubConfig create(Nameable project, String group, URI baseRepoUrl = URI.create(GITHUB_BASE_REPO_URL),
                               String credentialsId = null) {
        return new GitHubConfig(project, group, baseRepoUrl, credentialsId)
    }

    @Override
    protected URI createRepoUrl() {
        return URI.create("${gitServerUrl}/${group.toLowerCase()}/${project.name}.git")
    }
}
