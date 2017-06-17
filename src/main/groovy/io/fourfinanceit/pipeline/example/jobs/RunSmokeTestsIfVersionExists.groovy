package io.fourfinanceit.pipeline.example.jobs

import com.ofg.job.component.JobComponent
import io.fourfinanceit.pipeline.example.JenkinsVariable
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.common.MicroserviceVersion
import javaposse.jobdsl.dsl.Job

import static io.fourfinanceit.pipeline.example.MicroserviceJobUtils.configureGit
import static io.fourfinanceit.pipeline.example.common.component.InlineComponent.component

/**
 * @author Tomasz Uliński
 */
class RunSmokeTestsIfVersionExists extends AbstractRunSmokeTests {

    protected RunSmokeTestsIfVersionExists(MicroserviceVersion version) {
        super(version)
    }

    def versionReference = version.jenkinsVariable.reference

    private Closure versionReferenceIsNotEmpty() {
        return {
            shell("[ ! -z \"${versionReference}\" ]")
        }
    }

    @Override
    protected JobComponent<Job> configureScm(MicroserviceProject project) {
        return configureGit(project, JenkinsVariable.GIT_CHECKOUT_REF.reference)
    }

    @Override
    protected JobComponent<Job> configureSteps(MicroserviceProject project) {
        return component {
            environmentVariables {
                groovy(prepareGitRefGroovyScript(project))
            }
            steps {
                conditionalSteps {
                    condition versionReferenceIsNotEmpty()
                    runner('Fail')
                    steps runSmokeTestsGradleStep(project)
                }
            }
        }
    }

    private String prepareGitRefGroovyScript(MicroserviceProject project) {
        getClass().getResource('/io/fourfinanceit/gitRefGroovyScript.template').text
                .replace('%%DEFAULT_GIT_REF%%', JenkinsVariable.GIT_COMMIT.reference)
                .replace('%%PREV_APP_VERSION_REF%%', versionReference)
                .replace('%%RELEASE_TAG_PREFIX%%', project.releaseTagPrefix)
                .replace('%%GIT_CHECKOUT_REF_NAME%%', JenkinsVariable.GIT_CHECKOUT_REF.name())
    }

    @Override
    protected JobComponent<Job> configurePublishers() {
        return component {
            publishers {
                flexiblePublish {
                    conditionalAction {
                        condition versionReferenceIsNotEmpty()
                        publishers archiveJunitTestResults()
                    }
                }
            }
        }
    }
}
