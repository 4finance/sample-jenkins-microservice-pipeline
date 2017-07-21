package io.fourfinanceit.pipeline.example.common.component

import com.ofg.job.component.JobComponent
import javaposse.jobdsl.dsl.Job


class InlineComponent<J extends Job> implements JobComponent<J> {
    
    private final Closure configuration
    
    private InlineComponent(Closure configuration) {
        this.configuration = configuration
    }
    
    public static <J extends Job> InlineComponent<J> component(@DelegatesTo(J.class) Closure cfg) {
        return new InlineComponent<J>(cfg)
    }
    
    @Override
    void applyOn(J job) {
        configuration.delegate = job
        configuration.call()
    }
}
