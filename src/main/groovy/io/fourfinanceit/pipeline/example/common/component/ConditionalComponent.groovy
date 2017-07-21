package io.fourfinanceit.pipeline.example.common.component

import com.ofg.job.component.JobComponent
import javaposse.jobdsl.dsl.Job
import java.util.function.BooleanSupplier


class ConditionalComponent<J extends Job> implements JobComponent<J> {
    
    private final BooleanSupplier condition;
    private final JobComponent<J> delegate;
    
    private ConditionalComponent(BooleanSupplier condition, JobComponent<J> delegate) {
        this.condition = condition
        this.delegate = delegate
    }
    
    static <J extends Job> ConditionalComponentBuilder<J> use(JobComponent<J> delegate) {
        return new ConditionalComponentBuilder<J>(delegate)
    }
    
    @Override
    void applyOn(J job) {
        if (condition.asBoolean) {
            delegate.applyOn(job)
        }
    }
    
    static class ConditionalComponentBuilder<J extends Job> {
        
        private JobComponent<J> delegate
        
        private ConditionalComponentBuilder(JobComponent<J> delegate) {
            this.delegate = delegate
        }
        
        ConditionalComponent<J> when(BooleanSupplier condition) {
            return new ConditionalComponent<J>(
                condition,
                delegate
            )
        }
    }
}
