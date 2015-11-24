package io.fourfinanceit.pipeline.example.views

import io.fourfinanceit.pipeline.example.MicroserviceProject

/**
 * @author Marcin Grzejszczak
 */
class RealmProjectGrouper implements ProjectGrouper {

    @Override
    List<List<MicroserviceProject>> groupProjects(List<MicroserviceProject> projects) {
        return projects.groupBy { it.realmDomainPart() }.values() as List
    }
}
