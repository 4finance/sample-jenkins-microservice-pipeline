package io.fourfinanceit.pipeline.example.jobs

import com.ofg.pipeline.core.JenkinsVariables
import io.fourfinanceit.pipeline.example.MicroserviceJobUtils
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.common.Constants
import io.fourfinanceit.pipeline.example.common.Rundeck
import javaposse.jobdsl.dsl.Job

/**
 * @author Artur Gajowy
 * @author Nakul Mishra
 */
class DeployStubRunnerToSmokeTesting extends MicroserviceJobDefinition {
    @Override
    void configure(Job job, MicroserviceProject project, JenkinsVariables jenkinsVariables) {
        job.with {
            publishers Rundeck.invoke(
                    "deploy:deploy stubrunner",
                    withDeployStubRunnerDeploymentParamsFor(project)
            )
        }
    }

    private Map<String, String> withDeployStubRunnerDeploymentParamsFor(MicroserviceProject project) {
        return MicroserviceJobUtils.createDefaultDeploymentParams(project.groupId, project.smokeTestingEnv) + [
                app_name        : project.serviceName,
                app_version     : Constants.STUB_RUNNER_VERSION,
                app_wait_timeout: project.appTimeoutInStubRunner
        ]
    }
}
