package io.fourfinanceit.pipeline.example.views

import io.fourfinanceit.pipeline.example.MicroserviceProject

/**
 * @author Marcin Grzejszczak
 */
interface ProjectGrouper {
    List<List<MicroserviceProject>> groupProjects(List<MicroserviceProject> projects)
}
