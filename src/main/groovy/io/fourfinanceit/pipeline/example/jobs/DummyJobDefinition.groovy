package io.fourfinanceit.pipeline.example.jobs

import com.ofg.pipeline.core.JenkinsVariables
import io.fourfinanceit.pipeline.example.MicroserviceProject
import javaposse.jobdsl.dsl.Job

/**
 * @author Tomasz Uliński
 * @author Szymon Homa
 */
abstract class DummyJobDefinition extends MicroserviceJobDefinition {

    @Override
    void configure(Job job, MicroserviceProject microserviceProject, JenkinsVariables jenkinsVariables) {
        job.with {
            steps {
                shell(/echo "This is just a dummy placeholder for '${this.getJobLabel()}' job."/)
                shell(/echo "Will terminate in 3 seconds."/)
                shell(/sleep 3/)
                shell(/echo "Bye!"/)
            }
        }
    }
}
