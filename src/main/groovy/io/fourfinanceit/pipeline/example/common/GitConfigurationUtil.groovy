package io.fourfinanceit.pipeline.example.common

import com.ofg.pipeline.core.JenkinsVariables
import io.fourfinanceit.pipeline.example.JenkinsVariable
import io.fourfinanceit.pipeline.example.scm.ScmConfig
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.helpers.scm.GitContext

/**
 * @author Szymon Homa
 * @author Tomasz Uliński
 */
class GitConfigurationUtil {

    static void applyStandardGitConfigurationIfRequested(Job job, ScmConfig scmConfig,
                                                         JenkinsVariables jenkinsVariables) {
        if (scmConfig) {
            job.with {
                scm {
                    git {
                        remote {
                            url(scmConfig.repoUrl.toString())
                            credentials(scmConfig.credentialsId)
                        }
                        branch("*/master")
                    }
                }
                if (scmConfig.cronToPoll && jenkinsVariables.getBoolean(JenkinsVariable.SCM_POLLING_ENABLED, true)) {
                    triggers {
                        scm(scmConfig.cronToPoll)
                    }
                }
            }
        }
    }

    private static void addExtension(GitContext gitContext) {
        def option = gitContext.extensions[0].options[0]
        new Node(option, 'fastForwardMode', FastForwardMode.FF)
    }

}
