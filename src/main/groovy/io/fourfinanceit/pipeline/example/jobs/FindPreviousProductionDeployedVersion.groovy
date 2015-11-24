package io.fourfinanceit.pipeline.example.jobs

import com.ofg.pipeline.core.JenkinsVariables
import io.fourfinanceit.pipeline.example.JenkinsVariable
import io.fourfinanceit.pipeline.example.MicroserviceJobUtils
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.PipelineUtils
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext

/**
 * @author Tomasz Uliński
 * @author Łukasz Szczęsny
 */
class FindPreviousProductionDeployedVersion extends MicroserviceJobDefinition {

    private static final String RELEASE_TAG_GETTER_SCRIPT = FindPreviousProductionDeployedVersion
            .getResource('/io/fourfinanceit/pipeline/releaseTagGetter.groovy').text

    @Override
    void configure(Job job, MicroserviceProject project, JenkinsVariables jenkinsVariables) {
        job.with {
            //TODO: implement releaseTagGetter.groovy without checking out a git repo
            scm MicroserviceJobUtils.configureGit(project)
            environmentVariables((JenkinsVariable.MICROSERVICE_RELEASE_TAG_PREFIX): project.getFullReleaseTagPrefix())
            steps {
                systemGroovyCommand(RELEASE_TAG_GETTER_SCRIPT)
            }
            publishers {
                PipelineUtils.markBuildNoPreviousProductionVersionFound((PublisherContext) delegate)
            }
        }
    }
}
