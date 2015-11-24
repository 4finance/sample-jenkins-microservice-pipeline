package io.fourfinanceit.pipeline.example

import io.fourfinanceit.pipeline.example.common.MicroserviceVersion

/**
 * @author Tomasz Uliński
 * @author Szymon Homa
 * @author Artur Gajowy
 * @author Marcin Grzejszczak
 * @author Marcin Zajączkowski
 */
abstract class MicroserviceJobUtils {

    private static final String ARTIFACT_REPOSITORY_ID = "nexus"

    static Map<String, String> withServiceDeploymentParamsFor(MicroserviceProject project, String environment,
                                                              JenkinsVariable appVersionJenkinsVariable) {
        return createDefaultDeploymentParams(project.groupId, environment) + [
                app_artifact: project.effectiveArtifactId,
                app_name    : project.serviceName,
                app_version : appVersionJenkinsVariable.reference
        ]
    }

    static Map<String, String> createDefaultDeploymentParams(String groupId, String environment) {
        return [
                app_group_id: groupId,
                env         : environment
        ]
    }


    static Closure configureGit(MicroserviceProject project, String gitRef = 'master') {
        return {
            git {
                remote {
                    name('origin')
                    url(project.scmConfig.repoUrl.toString())
                    credentials(project.scmConfig.credentialsId)
                    branch(gitRef)
                }
            }
        }
    }

    static Closure configureGit(MicroserviceProject project, MicroserviceVersion version) {
        return configureGit(project, getGitRef(version, project))
    }

    private static String getGitRef(MicroserviceVersion version, MicroserviceProject project) {
        switch (version) {
            case MicroserviceVersion.CURRENT_VERSION:
                return JenkinsVariable.GIT_COMMIT.reference
            case MicroserviceVersion.PREVIOUS_VERSION:
                return "${project.fullReleaseTagPrefix}/${version.jenkinsVariable.reference}"
        }
    }


}