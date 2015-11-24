import com.ofg.pipeline.core.JenkinsVariables
import com.ofg.pipeline.core.PipelineTemplateBuilder
import io.fourfinanceit.pipeline.example.MicroservicePipelineTemplate
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.Nameable
import io.fourfinanceit.pipeline.example.scm.GitHubConfig
import io.fourfinanceit.pipeline.example.views.MicroserviceViewsBuilder
import javaposse.jobdsl.dsl.DslFactory

int port = 9090
List<String> projectNames = ['boot-microservice']

List<MicroserviceProject> projects = projectNames.collect { String name ->
        new MicroserviceProject(
                qualifiedProjectName: name,
                realDomainPart: 'pl',
                scmConfig: GitHubConfig.create({ name } as Nameable, '4finance'),
                qualifiedName: name,
                nameWithProduct: name,
                groupId: 'io.fourfinance.uservice',
                serviceName: name,
                smokeTestingEnv: 'smoke',
                appTimeoutInStubRunner: 5000,
                fullReleaseTagPrefix: 'release',
                prodEnv: 'prod',
                manualTestingEnv: false,
                notificationEmails: "$name@example.com",
                port: port,
                releaseTagPrefix: 'release'
        )
}

projects.each {
        new PipelineTemplateBuilder(this as DslFactory, JenkinsVariables.from(this))
                .build(MicroservicePipelineTemplate.INSTANCE, it)
}
new MicroserviceViewsBuilder(this as DslFactory).build(projects)
