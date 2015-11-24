import com.ofg.pipeline.core.JenkinsVariables
import com.ofg.pipeline.core.PipelineTemplateBuilder
import io.fourfinanceit.pipeline.example.GitProjectFetcher
import io.fourfinanceit.pipeline.example.MicroservicePipelineTemplate
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.Nameable
import io.fourfinanceit.pipeline.example.scm.GitHubConfig
import io.fourfinanceit.pipeline.example.views.MicroserviceViewsBuilder
import javaposse.jobdsl.dsl.DslFactory

String organization = binding.variables['ORGANIZATION_NAME'] ?: 'uservices-hackathon'
def reposApi = new URL("https://api.github.com/orgs/${organization}/repos")
def repos = new GitProjectFetcher(binding.variables['OFFLINE_MODE'] ?: true, reposApi).fetchRepos()
def projectsToExclude =  ((binding.variables['PROJECTS_TO_EXCLUDE'] as String)?.split(',') as List) ?: ['github.io', 'properties', 'jenkins-pipeline-dsl-example']

List projectToCode = repos.findAll {!projectsToExclude.contains(it.name)}

int port = 9090
List<MicroserviceProject> projects = projectToCode.collect {
        String name = it.name
        return new MicroserviceProject(
                qualifiedProjectName: name,
                realDomainPart: 'pl',
                scmConfig: GitHubConfig.create({ name } as Nameable, organization),
                qualifiedName: name,
                nameWithProduct: name,
                groupId: 'pl.uservices',
                serviceName: name,
                smokeTestingEnv: 'smoke',
                appTimeoutInStubRunner: 5000,
                fullReleaseTagPrefix: 'release',
                prodEnv: 'prod',
                manualTestingEnv: false,
                notificationEmails: 'asd@asd.com',
                port: port++,
                releaseTagPrefix: 'release'
        )
}
projects.each {
        new PipelineTemplateBuilder(this as DslFactory, JenkinsVariables.from(this))
                .build(MicroservicePipelineTemplate.INSTANCE, it)
}
new MicroserviceViewsBuilder(this as DslFactory).build(projects)