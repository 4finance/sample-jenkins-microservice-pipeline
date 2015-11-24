package io.fourfinanceit.pipeline.example.jobs

import io.fourfinanceit.pipeline.example.MicroserviceJobUtils
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.common.MicroserviceVersion
import javaposse.jobdsl.dsl.Job

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
    protected void configureScm(Job job, MicroserviceProject project) {
        job.with {
            scm MicroserviceJobUtils.configureGit(project, version)
        }
    }

    @Override
    protected void configureSteps(Job job, MicroserviceProject project) {
        job.with {
            steps runSmokeTestsGradleStep(project)
        }
    }

    @Override
    protected void configurePublishers(Job job) {
        job.with {
            publishers archiveJunitTestResults()
        }
    }
}
