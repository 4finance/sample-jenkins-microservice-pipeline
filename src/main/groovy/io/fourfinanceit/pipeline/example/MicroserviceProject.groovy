package io.fourfinanceit.pipeline.example
import com.ofg.pipeline.core.Project
import io.fourfinanceit.pipeline.example.scm.ScmConfig
import groovy.transform.Canonical

/**
 * @author Marcin Zajączkowski
 * @author Marcin Grzejszczak
 * @author Tomasz Uliński
 * @author Artur Gajowy
 */
@Canonical
class MicroserviceProject implements Project {

    String qualifiedName
    String qualifiedProjectName
    String realDomainPart
    String nameWithProduct
    ScmConfig scmConfig
    String groupId
    String serviceName
    String smokeTestingEnv
    Integer appTimeoutInStubRunner
    String fullReleaseTagPrefix
    String prodEnv
    String manualTestingEnv
    String notificationEmails
    Integer port
    String releaseTagPrefix

    String getQualifiedProjectName() {
        return qualifiedProjectName
    }

    String realmDomainPart() {
        return realDomainPart
    }

    String nameWithProduct() {
        return nameWithProduct
    }

    @Override
    String getQualifiedName() {
        return qualifiedName
    }

    String getEffectiveArtifactId() {
        return qualifiedProjectName - '.io'
    }
}
