package io.fourfinanceit.pipeline.example.jobs

import com.google.common.base.CaseFormat
import com.ofg.pipeline.core.JobDefinition
import io.fourfinanceit.pipeline.example.MicroserviceProject
import javaposse.jobdsl.dsl.Job

/**
 * @author Artur Gajowy
 */
abstract class MicroserviceJobDefinition extends JobDefinition<Job, MicroserviceProject> {

    private static final JOB_TYPE_FRAGMENT_SEPARATOR = '-'

    Class<Job> jobClass = Job

    protected final String camelCaseToJobTypeFormat(String camelCase) {
        def type = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelCase)
        return type.replaceAll('_', JOB_TYPE_FRAGMENT_SEPARATOR)
    }

}
