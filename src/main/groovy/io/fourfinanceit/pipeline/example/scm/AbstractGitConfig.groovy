package io.fourfinanceit.pipeline.example.scm

import io.fourfinanceit.pipeline.example.Nameable

/**
 * @author Marcin Zajączkowski
 * @author Nakul Mishra
 * @author Olga Maciaszek-Sharma
 */
abstract class AbstractGitConfig implements ScmConfig {

    private static final String DEFAULT_POLL_SCM_CRON_STRING = 'H/5 * * * *'

    protected URI gitServerUrl
    protected String group
    protected Nameable project
    private String credentialsId
    private String cronToPoll
    private URI repoUrlCached = null
    private ScmPrConfig prConfig

    AbstractGitConfig(Nameable project, String group, URI gitServerUrl, String credentialsId) {
        this.project = project
        this.gitServerUrl = gitServerUrl
        this.group = group
        this.credentialsId = credentialsId
        this.cronToPoll = DEFAULT_POLL_SCM_CRON_STRING
        this.prConfig = new ScmPrConfig()
    }

    void withBaseUrl(String baseUrl) {
        this.gitServerUrl = URI.create(baseUrl)
    }

    void withGroup(String group) {
        this.group = group
    }

    void withCronToPoll(String cronToPoll) {
        this.cronToPoll = cronToPoll
    }

    void withNoCronPolling() {
        this.cronToPoll = null
    }

    void withPrBuild(@DelegatesTo(ScmPrConfig) Closure closure) {
        prConfig = new ScmPrConfig()
        prConfig.with(closure)
    }

    //TODO: Verify @Memoized compliance with Groovy 1.8.9 in Jenkins
    @Override
    URI getRepoUrl() {
        if (!repoUrlCached) {
            repoUrlCached = createRepoUrl()
        }
        return repoUrlCached
    }

    protected abstract URI createRepoUrl()

    @Override
    URI gitServerUrl() {
        return gitServerUrl
    }

    @Override
    String getGroup() {
        return group
    }

    @Override
    //TODO this method should rather return Optional
    String getCredentialsId() {
        return credentialsId
    }

    @Override
    String getCronToPoll() {
        return cronToPoll
    }

    @Override
    //TODO this method should rather return Optional
    ScmPrConfig getPrConfig() {
        return prConfig
    }

    @Override
    String getProjectName() {
        return project.name
    }
}
