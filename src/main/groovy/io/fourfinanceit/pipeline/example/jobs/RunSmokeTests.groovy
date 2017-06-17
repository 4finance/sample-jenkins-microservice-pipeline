package io.fourfinanceit.pipeline.example.jobs

import com.ofg.job.component.JobComponent
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.common.MicroserviceVersion
import javaposse.jobdsl.dsl.Job

import static io.fourfinanceit.pipeline.example.MicroserviceJobUtils.configureGit
import static io.fourfinanceit.pipeline.example.common.component.InlineComponent.component

/**
 * @author Artur Gajowy
 * @author Tomasz Uliński
 * @author Szymon Homa
 */
class RunSmokeTests extends AbstractRunSmokeTests {

    protected RunSmokeTests(MicroserviceVersion version) {
        super(version)
    }

    @Override
    protected JobComponent<Job> configureScm(MicroserviceProject project) {
        return configureGit(project, version)
    }

    @Override
    protected JobComponent<Job> configureSteps(MicroserviceProject project) {
        return component{
            steps runSmokeTestsGradleStep(project)
        }
    }

    @Override
    protected JobComponent<Job> configurePublishers() {
        return component{
            publishers archiveJunitTestResults()
        }
    }
}
