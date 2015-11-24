package io.fourfinanceit.pipeline.example.jobs.standalone

import com.ofg.pipeline.core.JenkinsVariables
import io.fourfinanceit.pipeline.example.JenkinsVariable
import io.fourfinanceit.pipeline.example.MicroserviceJobUtils
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.common.Environment
import io.fourfinanceit.pipeline.example.common.Rundeck
import io.fourfinanceit.pipeline.example.jobs.MicroserviceJobDefinition
import javaposse.jobdsl.dsl.Job

/**
 * @author Marcin Zajączkowski
 * @author Olga Maciaszek-Sharma
 */
class DeployVersionToSelectedEnvironment extends MicroserviceJobDefinition {

    @Override
    void configure(Job job, MicroserviceProject project, JenkinsVariables jenkinsVariables) {
        job.with {
            parameters {
                stringParam(JenkinsVariable.APP_VERSION.name(), '', "Version of ${project.groupId}:${project.effectiveArtifactId} to deploy")
                stringParam(JenkinsVariable.DEPLOY_ENV.name(), Environment.MANUAL_TESTING.getFrom(project), 'Environment to deploy')
            }
            wrappers {
                buildName("#${JenkinsVariable.BUILD_NUMBER.reference} ${JenkinsVariable.APP_VERSION.envReference} ${JenkinsVariable.DEPLOY_ENV.envReference}")
            }
            publishers Rundeck.invoke(
                    Rundeck.DEPLOY_SERVICE_TASK,
                    MicroserviceJobUtils.withServiceDeploymentParamsFor(project, JenkinsVariable.DEPLOY_ENV.reference, JenkinsVariable.APP_VERSION)
            )
        }
    }
}
