package io.fourfinanceit.pipeline.example.common

import com.google.common.base.CaseFormat
import io.fourfinanceit.pipeline.example.MicroserviceProject

/**
 * @author Artur Gajowy
 */
enum Environment {
    SMOKE_TESTING({ MicroserviceProject project -> project.smokeTestingEnv }),
    PRODUCTION({ MicroserviceProject project -> project.prodEnv }),
    MANUAL_TESTING({ MicroserviceProject project -> project.manualTestingEnv })

    Environment(Closure<String> environmentAccessor) {
        this.environmentAccessor = environmentAccessor
    }

    private Closure<String> environmentAccessor

    String getLabel() {
        CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name())
    }

    String getFrom(MicroserviceProject project) {
        this.environmentAccessor(project)
    }
}