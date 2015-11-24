package io.fourfinanceit.pipeline.example.jobs

import com.ofg.pipeline.core.JenkinsVariables
import com.ofg.pipeline.core.JobType
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.ProjectVersion
import javaposse.jobdsl.dsl.Job

/**
 * @author Marcin Grzejszczak
 * @author Artur Gajowy
 * @author Tomasz Uliński
 * @author Olga Maciaszek-Sharma
 */
class DeployToProduction extends MicroserviceJobDefinition {

    private final ProjectVersion projectVersion

    DeployToProduction(ProjectVersion projectVersion) {
        this.projectVersion = projectVersion
    }

    @Override
    JobType getJobType() {
        return new JobType(toJobTypeFromUpperUnderscore('DEPLOY_' + projectVersion.name() + '_TO_PRODUCTION'))
    }

    @Override
    void configure(Job job, MicroserviceProject project, JenkinsVariables jenkinsVariables) {
        job.with {
            publishers {
                rundeck('deploy:deploy') {
                    options([
                            artifactId: project.effectiveArtifactId,
                            groupId: project.scmConfig.group,
                            nexusUrl: 'http://nexus.uservices.pl:8081/nexus/content/repositories/releases/',
                            version: '$PIPELINE_VERSION'

                    ])
                    shouldFailTheBuild()
                    shouldWaitForRundeckJob()
                }
            }
        }
    }
}
