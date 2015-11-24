package io.fourfinanceit.pipeline.example.scm

/**
 * @author Artur Gajowy
 * @author Marcin Zajączkowski
 * @author Marek Kapowicki
 */
class ScmPrConfig {

    enum TriggerMode {
        AUTO, PHRASE_IN_COMMENT

        boolean isOnDemand() {
            this == PHRASE_IN_COMMENT
        }
    }

    String cronToPollScm = "*/5 * * * *"
    TriggerMode triggerMode = TriggerMode.AUTO

    void withCronToPollScm(String cronToPollScm) {
        this.cronToPollScm = cronToPollScm
    }

    void withTriggerMode(TriggerMode triggerMode) {
        this.triggerMode = triggerMode
    }
}
