package io.fourfinanceit.pipeline.example.template

import com.ofg.pipeline.core.JenkinsVariables
import com.ofg.pipeline.core.PipelineTemplateBuilder
import io.fourfinanceit.pipeline.example.AbstractJobXmlComparingSpec
import io.fourfinanceit.pipeline.example.MicroservicePipelineTemplate
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.Nameable
import io.fourfinanceit.pipeline.example.scm.GitHubConfig
import io.fourfinanceit.pipeline.example.views.MicroserviceViewsBuilder
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Artur Gajowy
 * @author Szymon Homa
 * @author Marcin Grzejszczak
 */
class MicroservicePipelineTemplateBuilderSpec extends AbstractJobXmlComparingSpec {

    //TODO: Test also product and realm
    private static final String JOB_NAME = 'boot-microservice'
    private static final String PREFIX = "$JOB_NAME"

    List<String> expectedJobs = [
            "$PREFIX-build-and-publish",
            "$PREFIX-deploy-stub-runner-to-smoke-testing",
            "$PREFIX-deploy-current-version-to-smoke-testing",
            "$PREFIX-run-smoke-tests-for-current-version",
            "$PREFIX-find-previous-production-deployed-version",
            "$PREFIX-deploy-previous-production-version-to-smoke-testing",
            "$PREFIX-run-smoke-tests-for-previous-production-version",
            "$PREFIX-deploy-current-version-to-production",
            "$PREFIX-tag-release",
            "$PREFIX-deploy-previous-production-version-to-production",
//            "$PREFIX-deploy-version-to-selected-environment",
//            "$JOB_NAME-pr",
//            "$JOB_NAME-sonar",
    ]

    String expectedJobXmlsPath = '/jobs/'

    @Override
    void setupJobs(DslFactory dslFactory) {
        MicroserviceProject project = createProject {}
        new PipelineTemplateBuilder(dslFactory, JenkinsVariables.from([:]))
                .build(MicroservicePipelineTemplate.INSTANCE, project)
        new MicroserviceViewsBuilder(dslFactory).build([project])
    }

    private MicroserviceProject createProject(@DelegatesTo(MicroserviceProject) Closure closure = Closure.IDENTITY) {

        return new MicroserviceProject(
                qualifiedProjectName: JOB_NAME,
                realDomainPart: 'pl',
                scmConfig: GitHubConfig.create({ JOB_NAME } as Nameable, '4finance'),
                qualifiedName: JOB_NAME,
                nameWithProduct: JOB_NAME,
                groupId: 'io.fourfinance.uservice',
                serviceName: JOB_NAME,
                smokeTestingEnv: 'smoke',
                appTimeoutInStubRunner: 5000,
                fullReleaseTagPrefix: 'release',
                prodEnv: 'prod',
                manualTestingEnv: false,
                notificationEmails: "$JOB_NAME@example.com",
                port: 8888,
                releaseTagPrefix: 'release'
        )
    }
}
