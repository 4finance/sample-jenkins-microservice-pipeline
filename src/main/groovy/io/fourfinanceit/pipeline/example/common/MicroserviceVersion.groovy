package io.fourfinanceit.pipeline.example.common
import io.fourfinanceit.pipeline.example.JenkinsVariable

/**
 * @author Tomasz Uliński
 */
enum MicroserviceVersion {
    CURRENT_VERSION('CurrentVersion', JenkinsVariable.APP_VERSION),
    PREVIOUS_VERSION('PreviousProductionVersion', JenkinsVariable.PREV_APP_VERSION)

    final String label
    final JenkinsVariable jenkinsVariable

    private MicroserviceVersion(String label, JenkinsVariable jenkinsVariable) {
        this.label = label
        this.jenkinsVariable = jenkinsVariable
        verify()
    }

    private verify() {
        assert label != null
        assert !label.isEmpty() && Character.isUpperCase(label.charAt(0))
        assert jenkinsVariable != null
    }
}
