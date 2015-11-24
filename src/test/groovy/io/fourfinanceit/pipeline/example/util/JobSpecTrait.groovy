package io.fourfinanceit.pipeline.example.util

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobParent
import javaposse.jobdsl.dsl.MemoryJobManagement

/**
 * @author Marcin Grzejszczak
 * @author Marek Kapowicki
 * @author Nakul Mishra
 */
trait JobSpecTrait {

    JobParent createJobParent() {
        return createJobParent(new MemoryJobManagement())
    }

    JobParent createJobParent(JobManagement jobManagement) {
        JobParent jobParent = new TestJobParent()
        jobParent.setJm(jobManagement)
        return jobParent
    }

    static class TestJobParent extends JobParent {
        @Override
        Object run() {
            return null
        }
    }
}
