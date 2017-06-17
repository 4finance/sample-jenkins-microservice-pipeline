package io.fourfinanceit.pipeline.example.jobs

import com.ofg.job.component.JobComponent
import com.ofg.pipeline.core.JenkinsVariables
import io.fourfinanceit.pipeline.example.JobDslConfigureHacksUtil
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.common.component.InlineComponent
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.helpers.step.StepContext

import java.util.function.BooleanSupplier

import static com.ofg.job.component.triggers.PollSCMTrigger.onCron
import static io.fourfinanceit.pipeline.example.JenkinsVariable.APP_VERSION
import static io.fourfinanceit.pipeline.example.JenkinsVariable.BUILD_DATETIME
import static io.fourfinanceit.pipeline.example.JenkinsVariable.BUILD_DISPLAY_NAME
import static io.fourfinanceit.pipeline.example.JenkinsVariable.GIT_COMMIT
import static io.fourfinanceit.pipeline.example.JenkinsVariable.GIT_COMMIT_SHORT
import static io.fourfinanceit.pipeline.example.JenkinsVariable.PIPELINE_VERSION
import static io.fourfinanceit.pipeline.example.JenkinsVariable.SCM_POLLING_ENABLED
import static io.fourfinanceit.pipeline.example.MicroserviceJobUtils.configureGit
import static io.fourfinanceit.pipeline.example.common.component.ConditionalComponent.use
import static io.fourfinanceit.pipeline.example.common.component.InlineComponent.component

/**
 * @author Artur Gajowy
 * @author Marcin Zajączkowski
 * @author Marcin Grzejszczak
 * @author Marek Kapowicki
 */
class BuildAndPublish extends MicroserviceJobDefinition {
    
    @Override
    void configure(Job job, MicroserviceProject project, JenkinsVariables jenkinsVariables) {
        (
            setupEnvironmentVariables() &
                maskPasswords() &
                configureGit(project) &
                use(onCron(project.scmConfig.cronToPoll))
                    .when(scmPoolingEnabled(jenkinsVariables)) &
                buildProject() &
                publishArtifacts()
        ).applyOn(job)
    }
    
    private JobComponent<Job> setupEnvironmentVariables() {
        return component {
            environmentVariables {
                groovy($/
                    String buildDateTime = new Date().format("yyyyMMdd-HHmmss")
                    return [
                        ${BUILD_DATETIME.name()}: buildDateTime,
                        ${PIPELINE_VERSION.name()}: "${BUILD_DISPLAY_NAME.reference} $${buildDateTime}",
                    ]
                /$.stripIndent())
            }
            def gitShortCommitFileName = 'jenkinsShortCommitHashHack.txt'
            steps {
                //TODO: Workaround for https://issues.jenkins-ci.org/browse/JENKINS-30042
                shell(/echo "${GIT_COMMIT_SHORT.name()}=`echo ${GIT_COMMIT.reference} | cut -c1-7`" > $gitShortCommitFileName/)
                environmentVariables {
                    propertiesFile(gitShortCommitFileName)
                    env(APP_VERSION.name(), "${BUILD_DATETIME.reference}-${GIT_COMMIT_SHORT.reference}")
                }
            }
        }
    }
    
    private JobComponent<Job> maskPasswords() {
        return component {
            configure JobDslConfigureHacksUtil.injectAndMaskPasswords()
        }
    }
    
    private BooleanSupplier scmPoolingEnabled(JenkinsVariables jenkinsVariables) {
        return { jenkinsVariables.getBoolean(SCM_POLLING_ENABLED, true) }
    }
    
    private JobComponent<Job> buildProject() {
        String initScriptName = 'adapt_gradle_build_for_microservice_pipeline.gradle'
        return component {
            steps {
                shell("cat > $initScriptName <<- EOF\n${loadScript("/io/fourfinanceit/pipeline/$initScriptName")}\nEOF")
                gradle(
                    "clean build --init-script $initScriptName --continue --stacktrace --parallel " +
                        projectVersionParameters()
                )
            }
        }
    }
    
    String loadScript(String scriptLocation) {
        return getClass()
            .getResource(scriptLocation)
            .text
    }
    
    private String projectVersionParameters() {
        /-PcurrentVersion=${APP_VERSION.reference}/
    }
    
    private JobComponent<Job> publishArtifacts() {
        return component {
            steps {
                ifBuildStillSuccessful(delegate as StepContext) {
                    gradle("publish --stacktrace ${projectVersionParameters()}")
                }
            }
        }
    }
    
    private static void ifBuildStillSuccessful(StepContext context, @DelegatesTo(StepContext) Closure closure) {
        context.with {
            conditionalSteps {
                condition {
                    status('SUCCESS', 'SUCCESS')
                }
                runner('Fail')
                steps closure
            }
        }
    }
}
