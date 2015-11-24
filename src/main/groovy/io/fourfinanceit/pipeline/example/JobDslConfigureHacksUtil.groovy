package io.fourfinanceit.pipeline.example

/**
 * @author Marcin Zajączkowski
 * @author Marek Kapowicki
 */
//TODO: Remove when available upstream
class JobDslConfigureHacksUtil {

    static Closure compactColumns() {
        return { Node listViewNode ->    //Replace with native support for Compact Columns when available
            listViewNode / 'columns' << 'com.robestone.hudson.compactcolumns.AllStatusesColumn' {
                colorblindHint('nohint')
                timeAgoTypeString('DIFF')
                onlyShowLastStatus(false)
                hideDays(0)
            }
        }
    }

    static Closure additionalDeliveryPipelineOptions() {
        return { Node deliveryPipelineViewNode ->
            deliveryPipelineViewNode << {
                allowPipelineStart(true)
                allowRebuild(true)
                showTotalBuildTime(true)
            }
        }
    }

    static Closure injectAndMaskPasswords() {
        //injectPasswords is supported by Job-DSL, but 'maskPasswordParameters' is not
        return { Node projectNode ->
            projectNode / 'buildWrappers' / 'EnvInjectPasswordWrapper' << {
                'injectGlobalPasswords'(true)
                'passwordEntries'()
                'maskPasswordParameters'(true)
            }
        }
    }
}
