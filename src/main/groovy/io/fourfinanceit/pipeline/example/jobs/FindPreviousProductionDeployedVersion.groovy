package io.fourfinanceit.pipeline.example.jobs

import com.ofg.pipeline.core.JenkinsVariables
import io.fourfinanceit.pipeline.example.JenkinsVariable
import io.fourfinanceit.pipeline.example.MicroserviceJobUtils
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.PipelineUtils
import io.fourfinanceit.pipeline.example.common.component.InlineComponent
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext

import static io.fourfinanceit.pipeline.example.MicroserviceJobUtils.configureGit
import static io.fourfinanceit.pipeline.example.common.component.InlineComponent.component

/**
 * @author Tomasz Uliński
 * @author Łukasz Szczęsny
 */
class FindPreviousProductionDeployedVersion extends MicroserviceJobDefinition {
    
    private static final String RELEASE_TAG_GETTER_SCRIPT = FindPreviousProductionDeployedVersion
        .getResource('/io/fourfinanceit/pipeline/releaseTagGetter.groovy').text
    
    @Override
    void configure(Job job, MicroserviceProject project, JenkinsVariables jenkinsVariables) {
        //TODO: implement releaseTagGetter.groovy without checking out a git repo
        (
            configureGit(project) &
                component {
                    environmentVariables((JenkinsVariable.MICROSERVICE_RELEASE_TAG_PREFIX): project.getFullReleaseTagPrefix())
                    steps {
                        systemGroovyCommand(RELEASE_TAG_GETTER_SCRIPT)
                    }
                } &
                component {
                    publishers {
                        PipelineUtils.markBuildNoPreviousProductionVersionFound((PublisherContext) delegate)
                    }
                }
        ).applyOn(job)
    }
}
