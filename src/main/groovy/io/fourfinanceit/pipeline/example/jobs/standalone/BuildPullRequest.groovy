package io.fourfinanceit.pipeline.example.jobs.standalone

import com.ofg.pipeline.core.JobType
import io.fourfinanceit.pipeline.example.jobs.DummyJobDefinition

/**
 * @author Marcin Grzejszczak
 */
class BuildPullRequest extends DummyJobDefinition {

    @Override
    JobType getJobType() {
        return new JobType('pr')
    }
}
