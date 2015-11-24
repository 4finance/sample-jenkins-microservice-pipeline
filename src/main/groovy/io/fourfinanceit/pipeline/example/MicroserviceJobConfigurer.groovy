package io.fourfinanceit.pipeline.example

import com.ofg.pipeline.core.JobConfigurer
import javaposse.jobdsl.dsl.Job

/**
 * @author Marcin Grzejszczak
 * @author Marek Kapowicki
 */
class MicroserviceJobConfigurer implements JobConfigurer<MicroserviceProject> {
    @Override
    void preConfigure(Job job, MicroserviceProject microserviceProject) {

    }

    @Override
    void postConfigure(Job job, MicroserviceProject microserviceProject) {
        job.with {
            configure { Node rootProject ->
                appendSlackNotification(rootProject)
            }
        }
    }

    void appendSlackNotification(Node rootNode) {
        Node propertiesNode = rootNode / 'properties'
        def slack = propertiesNode / 'jenkins.plugins.slack.SlackNotifier_-SlackJobProperty'
        (slack / 'startNotification').setValue(true)
        (slack / 'notifySuccess').setValue(true)
        (slack / 'notifyAborted').setValue(true)
        (slack / 'notifyNotBuilt').setValue(true)
        (slack / 'notifyUnstable').setValue(true)
        (slack / 'notifyFailure').setValue(true)
        (slack / 'notifyBackToNormal').setValue(true)
        (slack / 'notifyRepeatedFailure').setValue(true)
        (slack / 'includeTestSummary').setValue(true)
        (slack / 'showCommitList').setValue(true)
        def publishers = (rootNode / 'publishers')
        publishers / 'jenkins.plugins.slack.SlackNotifier'
    }
}
