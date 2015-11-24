package io.fourfinanceit.pipeline.example.jobs

import io.fourfinanceit.pipeline.example.JenkinsVariable
import io.fourfinanceit.pipeline.example.MicroserviceJobUtils
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.common.MicroserviceVersion
import javaposse.jobdsl.dsl.Job

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
    protected void configureScm(Job job, MicroserviceProject project) {
        job.with {
            scm MicroserviceJobUtils.configureGit(project, JenkinsVariable.GIT_CHECKOUT_REF.reference)
        }
    }

    @Override
    protected void configureSteps(Job job, MicroserviceProject project) {
        job.with {
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
    protected void configurePublishers(Job job) {
        job.with {
            publishers {
                flexiblePublish {
                    condition versionReferenceIsNotEmpty()
                    publisher archiveJunitTestResults()
                }
            }
        }
    }
}
