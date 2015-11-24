package io.fourfinanceit.pipeline.example
import com.ofg.pipeline.core.TriggerCondition
import javaposse.jobdsl.dsl.helpers.publisher.DownstreamTriggerContext
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import javaposse.jobdsl.dsl.views.ListView

/**
 * @author Artur Gajowy
 * @author Marcin Zajączkowski
 * @author Szymon Homa
 * @author Nakul Mishra
 */
class PipelineUtils {

    private static final String NO_PREVIOUS_VERSION_WARNING_TEXT = 'No previous production version has been found. ' +
            'Skipping rollback test steps.'

    static Closure downstreamParametrized(
            String projectName,
            TriggerCondition triggerThreshold = TriggerCondition.SUCCESS,
            List<String> propertiesFileNames = [],
            Map<String, String> predefinedProperties = [:]
    ) {
        return {
            downstreamParameterized {
                trigger("${projectName}", triggerThreshold.name(), true) {
                    downstreamBuildParameters(delegate)
                    if (predefinedProperties) {
                        predefinedProps(predefinedProperties)
                    }
                    propertiesFileNames.each {
                        propertiesFile(it)
                    }
                }
            }
        }
    }

    static Closure manualDownstreamParametrized(String projectName) {
        return {
            buildPipelineTrigger("$projectName") {
                parameters {
                    downstreamBuildParameters(delegate)
                }
            }
        }
    }

    private static void downstreamBuildParameters(DownstreamTriggerContext delegate) {
        delegate.with {
            currentBuild()
            sameNode()
        }
    }

    static void markBuildNoPreviousProductionVersionFound(PublisherContext publisherContext,
                                                          JenkinsVariable appVersionEnvVariableName = JenkinsVariable.PREV_APP_VERSION) {
        publisherContext.with {
            groovyPostBuild("""
if (!manager.getEnvVariable('$appVersionEnvVariableName')) {
    manager.addWarningBadge('$NO_PREVIOUS_VERSION_WARNING_TEXT')
    manager.createSummary('warning.gif').appendText('<h2>$NO_PREVIOUS_VERSION_WARNING_TEXT</h2>', false, false, false, 'orange')
    manager.buildUnstable()
}
""", PublisherContext.Behavior.MarkFailed)
            //full class reference to not fail with "No such property: Behavior" in Groovy 2.x
        }   //TODO: Add sandboxing when available - https://github.com/jenkinsci/job-dsl-plugin/pull/539
    }

    static Closure defaultColumns(ListView listView) {
        listView.with {
            columns {
                status()
                weather()
                name()
            }
            configure JobDslConfigureHacksUtil.compactColumns()
            columns {
                lastDuration()
                buildButton()
                lastBuildConsole()
            }
        }
    }
}
