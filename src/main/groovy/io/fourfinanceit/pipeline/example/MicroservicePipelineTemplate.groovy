package io.fourfinanceit.pipeline.example

import com.ofg.pipeline.core.JobChain
import com.ofg.pipeline.core.JobConfigurer
import com.ofg.pipeline.core.PipelineBuilder
import com.ofg.pipeline.core.PipelineTemplate
import com.ofg.pipeline.core.StageNameConfigurer
import com.ofg.pipeline.core.TriggerCondition
import com.ofg.pipeline.core.link.CombinedJobChainLink

import static JenkinsVariable.APP_VERSION
import static JenkinsVariable.GIT_COMMIT
import static JenkinsVariable.PREV_APP_VERSION
import static com.ofg.pipeline.core.link.ManualLink.manual
import static io.fourfinanceit.pipeline.example.jobs.Jobs.*
import static com.ofg.pipeline.core.link.AutoLink.auto

/**
 * @author Tomasz Uliński
 * @author Artur Gajowy
 * @author Szymon Homa
 * @author Nakul Mishra
 */
class MicroservicePipelineTemplate implements PipelineTemplate<MicroserviceProject> {

    static final INSTANCE = new MicroservicePipelineTemplate()

    private MicroservicePipelineTemplate() {}

    @Override
    JobConfigurer<MicroserviceProject> createJobConfigurer() {
        return new MicroserviceJobConfigurer()
    }

    @Override
    StageNameConfigurer createStageNameConfigurer() {
        return new DeliveryPipelineStageNameConfigurer()
    }

    @Override
    void configurePipeline(PipelineBuilder<MicroserviceProject> pipelineBuilder, MicroserviceProject project) {
        pipelineBuilder.configure {
            stage('Build') {
                job BUILD_AND_PUBLISH
            }
            stage('Run smoke tests & rollback scenario') {
                job DEPLOY_STUBRUNNER_TO_SMOKE_TESTING
                job DEPLOY_CURRENT_VERSION_TO_SMOKE_TESTING
                job RUN_SMOKE_TESTS_FOR_CURRENT_VERSION
                job FIND_PREVIOUS_PRODUCTION_DEPLOYED_VERSION
                job DEPLOY_PREVIOUS_VERSION_TO_SMOKE_TESTING
                job RUN_SMOKE_TESTS_FOR_PREVIOUS_VERSION
            }
            stage('Deploy to prod') {
                job DEPLOY_CURRENT_VERSION_TO_PROD
                job TAG_RELEASE
            }
            stage('Automatic rollback') {
                job DEPLOY_PREVIOUS_VERSION_TO_PROD
            }

            chain(JobChain.of(BUILD_AND_PUBLISH)
                    .then(auto(DEPLOY_STUBRUNNER_TO_SMOKE_TESTING)
                    .withPredefinedProperties(APP_VERSION, GIT_COMMIT)
            )
                    .then(DEPLOY_CURRENT_VERSION_TO_SMOKE_TESTING)
                    .then(RUN_SMOKE_TESTS_FOR_CURRENT_VERSION)
                    .then(FIND_PREVIOUS_PRODUCTION_DEPLOYED_VERSION)
                    .then(auto(DEPLOY_PREVIOUS_VERSION_TO_SMOKE_TESTING, TriggerCondition.UNSTABLE_OR_BETTER)
                    .withPredefinedProperties(PREV_APP_VERSION)
            )
                    .then(RUN_SMOKE_TESTS_FOR_PREVIOUS_VERSION)
                    .then(manual(DEPLOY_CURRENT_VERSION_TO_PROD))
                    .then(CombinedJobChainLink.of(
                    auto(TAG_RELEASE, TriggerCondition.SUCCESS),
                    auto(DEPLOY_PREVIOUS_VERSION_TO_PROD, TriggerCondition.UNSTABLE_OR_WORSE)
            ))
            )
        }
    }
}
