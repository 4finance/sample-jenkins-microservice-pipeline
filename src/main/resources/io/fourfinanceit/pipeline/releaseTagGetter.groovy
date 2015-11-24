package io.fourfinanceit.pipeline

import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.google.common.base.Optional
import com.google.common.collect.ImmutableList
import hudson.model.ParametersAction
import hudson.model.StringParameterValue
import hudson.plugins.git.GitException
import jenkins.model.Jenkins

import static hudson.security.ACL.SYSTEM

def getCredentials(String credentialsDescription) {
    def jenkins = Jenkins.getInstance()
    for (credentialsProvider in jenkins.getExtensionList(CredentialsProvider.class)) {
        for (credentials in credentialsProvider.getCredentials(StandardCredentials.class, jenkins, SYSTEM)) {
            if (credentials.getDescription() == credentialsDescription || credentials.getId() == credentialsDescription) {
                return credentials;
            }
        }
    }
}

def Optional<String> getNewestReleaseTag(Collection<String> tags, String tagPrefix) {
    tagPrefix = "${tagPrefix}/"
    def tagNames = tags.findAll { it.startsWith(tagPrefix) }
            .collect { it.substring(tagPrefix.length()) }
            .findAll { it.matches("\\d{8}-\\d{6}-\\w{7}") }
    tagNames.sort(Collections.reverseOrder())
    return tagNames.size() > 0 ? Optional.of(tagNames.first()) : Optional.absent()
}

def project = build.getProject()
def gitConfigs = project.scm.getUserRemoteConfigs()
def gitUrl = gitConfigs.first().getUrl()
def gitCredentials = gitConfigs.first().getCredentialsId()

def gitClient = org.jenkinsci.plugins.gitclient.Git
        .with(null, null)
        .using("/usr/bin/git")
        .getClient()

gitClient.addCredentials(gitUrl, getCredentials(gitCredentials))

final Collection<String> tags
try {
    tags = gitClient.getRemoteReferences(gitUrl, "*", false, true).keySet()
} catch (GitException e) {
    // catching this exception is necessary due to bug: JENKINS-30589
    tags = ImmutableList.of()
}

//TODO: maybe we should extend the classpath and use a JenkinsVariable constant here
def newestReleaseTag = getNewestReleaseTag(tags, build.getEnvironment(listener).get('MICROSERVICE_RELEASE_TAG_PREFIX'))

println "release tag: $newestReleaseTag"

// setting env variable
// We have to set PREV_APP_VERSION parameter always, even when previous version does not exist
// due to JENKINS-10779 issue.
def pa = new ParametersAction([
        new StringParameterValue('PREV_APP_VERSION', newestReleaseTag.or(''))
])

// add variable to current job
Thread.currentThread().executable.addAction(pa)