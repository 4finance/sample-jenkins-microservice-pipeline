package io.fourfinanceit.pipeline.example.jobs

import com.google.common.base.CaseFormat
import com.google.common.base.Preconditions
import com.ofg.pipeline.core.JenkinsVariables
import com.ofg.pipeline.core.JobType
import io.fourfinanceit.pipeline.example.JenkinsVariable
import io.fourfinanceit.pipeline.example.MicroserviceJobUtils
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.common.Environment
import io.fourfinanceit.pipeline.example.common.MicroserviceVersion
import io.fourfinanceit.pipeline.example.common.Rundeck
import javaposse.jobdsl.dsl.Job

/**
 * @author Tomasz Uliński
 * @author Szymon Homa
 * @author Artur Gajowy
 * @author Marek Kapowicki
 */
class DeployVersionToEnvironment extends MicroserviceJobDefinition {

    private final MicroserviceVersion version
    private final Environment environment
    private final boolean checkIfVersionExists
    private final boolean sendTeamNotificationOnSuccess

    private DeployVersionToEnvironment(Builder builder) {
        this.version = Preconditions.checkNotNull(builder.version)
        this.environment = Preconditions.checkNotNull(builder.environment)
        this.checkIfVersionExists = builder.checkIfVersionExists
        this.sendTeamNotificationOnSuccess = builder.sendTeamNotificationOnSuccess
    }

    public static class Builder {
        private MicroserviceVersion version
        private Environment environment
        private boolean checkIfVersionExists = false
        private boolean sendTeamNotificationOnSuccess = false

        Builder setVersion(MicroserviceVersion version) {
            this.version = version
            return this
        }

        Builder setEnvironment(Environment environment) {
            this.environment = environment
            return this
        }

        Builder checkIfVersionExists() {
            this.checkIfVersionExists = true
            return this
        }

        Builder sendTeamNotificationOnSuccess() {
            this.sendTeamNotificationOnSuccess = true
            return this
        }

        DeployVersionToEnvironment build() {
            return new DeployVersionToEnvironment(this)
        }
    }

    public static Builder builder() {
        return new Builder()
    }

    @Override
    public final JobType getJobType() {
        return new JobType(camelCaseToJobTypeFormat("Deploy${version.label}To${environment.label}"))
    }

    @Override
    void configure(Job job, MicroserviceProject project, JenkinsVariables jenkinsVariables) {
        configureRundeckPublisher(job, project)
        configureSendingNotificationsOnSuccess(job, project)
    }

    private void configureRundeckPublisher(Job job, MicroserviceProject project) {
        job.with {
            if (checkIfVersionExists) {
                publishers {
                    flexiblePublish {
                        conditionalAction {
                            condition {
                                shell("[ ! -z \"${version.jenkinsVariable.reference}\" ]")
                            }
                            publishers rundeckDeploy(project)
                        }
                    }
                }
            } else {
                publishers rundeckDeploy(project)
            }
        }
    }

    private Closure rundeckDeploy(MicroserviceProject project) {
        return Rundeck.invoke(
                Rundeck.DEPLOY_SERVICE_TASK,
                MicroserviceJobUtils.withServiceDeploymentParamsFor(
                        project,
                        environment.getFrom(project),
                        version.jenkinsVariable
                )
        )
    }

    private void configureSendingNotificationsOnSuccess(Job job, MicroserviceProject project) {
        if (sendTeamNotificationOnSuccess) {
            job.with {
                publishers {
                    flexiblePublish {
                        conditionalAction {
                            condition sendTeamNotificationCondition()
                            publishers {
                                String additionalRecipients = project.notificationEmails.join(', ')

                                extendedEmail {
                                    if (additionalRecipients) {
                                        recipientList additionalRecipients
                                    }
                                    triggers {
                                        success {
                                            subject notificationMessageSubject(project)
                                            sendTo {
                                                requester()
                                                recipientList()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Closure sendTeamNotificationCondition() {
        if (checkIfVersionExists) return {
            and(
                    {shell("[ ! -z \"${version.jenkinsVariable.reference}\" ]")},
                    {shell("[ ! \"$JenkinsVariable.SKIP_TEAM_NOTIFICATIONS.reference\" = true ]")}
            )
        } else return {
            shell("[ ! \"$JenkinsVariable.SKIP_TEAM_NOTIFICATIONS.reference\" = true ]")
        }
    }

    private String notificationMessageSubject(MicroserviceProject project) {
        def projectName = project.qualifiedProjectName
        def versionName = camelCaseToReadableFormat(version.label)
        def versionVariable = version.jenkinsVariable.reference
        def env = environment.getFrom(project)
        return "$projectName $versionName $versionVariable successfully deployed to $env"
    }

    //TODO: mv to utils
    private static String camelCaseToReadableFormat(String camelCaseStr) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelCaseStr).replaceAll('_', ' ')
    }
}
