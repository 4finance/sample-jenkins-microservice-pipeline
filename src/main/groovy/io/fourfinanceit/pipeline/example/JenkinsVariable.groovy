package io.fourfinanceit.pipeline.example

import com.ofg.pipeline.core.JenkinsVariables
import com.ofg.pipeline.core.Variable

/**
 * @author Tomasz Uliński
 * @author Marcin Zajączkowski
 * @author Artur Gajowy
 * @author Olga Maciaszek-Sharma
 */
enum JenkinsVariable implements Variable {

    APP_VERSION,
    PREV_APP_VERSION,
    GIT_COMMIT,
    BUILD_DATETIME,
    BUILD_DISPLAY_NAME,
    GIT_COMMIT_SHORT,
    PIPELINE_VERSION,
    BUILD_PHRASE,
    AUTOMATIC_PR_BUILDING_ENABLED,
    SCM_POLLING_ENABLED,
    MICROSERVICE_RELEASE_TAG_PREFIX,
    SKIP_TEAM_NOTIFICATIONS,
    DEPLOY_ENV,
    BUILD_NUMBER,
    GIT_CHECKOUT_REF,
    ENV_IS_BUILD_SERVER_INSTANCE

    final String reference
    final String envReference

    JenkinsVariable() {
        this.reference = JenkinsVariables.reference(this)
        this.envReference = JenkinsVariables.envReference(this)
    }
}
