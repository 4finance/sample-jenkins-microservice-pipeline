package io.fourfinanceit.pipeline.example

import com.ofg.pipeline.core.StageNameConfigurer
import javaposse.jobdsl.dsl.Job

import static JenkinsVariable.*

/**
 * @author Artur Gajowy
 * @author Marek Kapowicki
 */
class DeliveryPipelineStageNameConfigurer implements StageNameConfigurer {

    @Override
    void configure(Job job, String stageName, String jobLabel) {
        job.with {
            deliveryPipelineConfiguration(stageName, jobLabel)
            wrappers {
                deliveryPipelineVersion(PIPELINE_VERSION.envReference, true)
            }
        }
    }
}