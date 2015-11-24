package io.fourfinanceit.pipeline.example.jobs

import io.fourfinanceit.pipeline.example.common.Environment
import io.fourfinanceit.pipeline.example.jobs.standalone.BuildPullRequest
import io.fourfinanceit.pipeline.example.jobs.standalone.DeployVersionToSelectedEnvironment

import static Environment.PRODUCTION
import static Environment.SMOKE_TESTING
import static io.fourfinanceit.pipeline.example.common.MicroserviceVersion.CURRENT_VERSION
import static io.fourfinanceit.pipeline.example.common.MicroserviceVersion.PREVIOUS_VERSION

/**
 * @author Artur Gajowy
 * @author Tomasz Uliński
 */
class Jobs {
    public static final BUILD_AND_PUBLISH =
            new BuildAndPublish()

    public static final DEPLOY_STUBRUNNER_TO_SMOKE_TESTING =
            new DeployStubRunnerToSmokeTesting()

    public static final DEPLOY_CURRENT_VERSION_TO_SMOKE_TESTING = DeployVersionToEnvironment.builder()
            .setVersion(CURRENT_VERSION)
            .setEnvironment(SMOKE_TESTING)
            .build()

    public static final RUN_SMOKE_TESTS_FOR_CURRENT_VERSION =
            new RunSmokeTests(CURRENT_VERSION)

    public static final DEPLOY_PREVIOUS_VERSION_TO_SMOKE_TESTING = DeployVersionToEnvironment.builder()
            .setVersion(PREVIOUS_VERSION)
            .setEnvironment(SMOKE_TESTING)
            .checkIfVersionExists()
            .build()

    public static final FIND_PREVIOUS_PRODUCTION_DEPLOYED_VERSION =
            new FindPreviousProductionDeployedVersion()

    public static final RUN_SMOKE_TESTS_FOR_PREVIOUS_VERSION =
            new RunSmokeTestsIfVersionExists(PREVIOUS_VERSION)

    public static final DEPLOY_CURRENT_VERSION_TO_PROD = DeployVersionToEnvironment.builder()
            .setVersion(CURRENT_VERSION)
            .setEnvironment(PRODUCTION)
            .sendTeamNotificationOnSuccess()
            .build()

    public static final TAG_RELEASE =
            new TagRelease()

    public static final DEPLOY_PREVIOUS_VERSION_TO_PROD = DeployVersionToEnvironment.builder()
            .setVersion(PREVIOUS_VERSION)
            .setEnvironment(PRODUCTION)
            .checkIfVersionExists()
            .sendTeamNotificationOnSuccess()
            .build()

    public static final DEPLOY_VERSION_TO_SELECTED_ENVIRONMENT = new DeployVersionToSelectedEnvironment()

    public static final BUILD_PULL_REQUEST = new BuildPullRequest()
}
