package io.fourfinanceit.pipeline.example.views

import io.fourfinanceit.pipeline.example.JobDslConfigureHacksUtil
import io.fourfinanceit.pipeline.example.MicroserviceProject
import io.fourfinanceit.pipeline.example.PipelineUtils
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.views.ListView
import javaposse.jobdsl.dsl.views.NestedView

/**
 * @author Artur Gajowy
 * @author Marcin Zajączkowski
 * @author Marcin Grzejszczak
 * @author Tomasz Uliński
 * @author Szymon Homa
 */
class MicroserviceViewsBuilder {

    private final DslFactory dslFactory
    private final ProjectGrouper realmProjectGrouper = new RealmProjectGrouper()

    MicroserviceViewsBuilder(DslFactory dslFactory) {
        this.dslFactory = dslFactory
    }

    void build(List<MicroserviceProject> projects) {
        NestedView microserviceView = dslFactory.nestedView("microservice")
        buildRealmBasedViews(microserviceView, realmProjectGrouper.groupProjects(projects))
    }

    //TODO: Add additional views if needed (like per project view - the same project for different realms/countries)
    private void buildRealmBasedViews(NestedView parentView, List<List<MicroserviceProject>> projectsByRealm) {
        projectsByRealm.each { List<MicroserviceProject> projects ->
            parentView.views {
                String realm = projects[0].realmDomainPart()  //there has to be at least elements for real to create a sublist
                nestedView(realm) {
                    views {
                        listView("${realm}-pr-builds") {
                            jobs {
                                //projects.each {} doesn't work probably due to default resolve strategy - https://groups.google.com/d/msg/job-dsl-plugin/RGQgOmRQo-4/mNJoMLRqb9AJ
                                for (MicroserviceProject config : projects) {
                                    name("${config.qualifiedProjectName}-pr")
                                }
                            }
                            PipelineUtils.defaultColumns((ListView)delegate)
                         }
                        listView("${realm}-manual-deploy-builds") {
                            jobs {
                                for (MicroserviceProject config : projects) {
                                    name("${config.qualifiedProjectName}-deploy-version-to-selected-environment")
                                }
                            }
                            PipelineUtils.defaultColumns((ListView)delegate)
                        }
                        nestedView("${realm}-delivery") {
                            views {
                                for (MicroserviceProject config : projects) {
                                    deliveryPipelineView("${realm}-delivery-${config.nameWithProduct()}") {
                                        pipelineInstances(5)
                                        columns(2)
                                        updateInterval(5)
                                        enableManualTriggers()
                                        showAvatars()
                                        showChangeLog()
                                        pipelines {
                                            component("Trigger pipeline for ${config.qualifiedProjectName}", "${config.qualifiedProjectName}-build-and-publish")
                                        }
                                        configure JobDslConfigureHacksUtil.additionalDeliveryPipelineOptions()
                                    }
                                }
                            }
                        }
                        deliveryPipelineView("${realm}-delivery-aggregated") {
                            pipelineInstances(0)
                            showAggregatedPipeline()
                            columns(1)
                            updateInterval(5)
                            enableManualTriggers()
                            showAvatars()
                            showChangeLog()
                            pipelines {
                                projects.each { MicroserviceProject project ->
                                    component("Trigger pipeline for ${project.qualifiedProjectName}", "${project.qualifiedProjectName}-build-and-publish")
                                }
                            }
                            configure JobDslConfigureHacksUtil.additionalDeliveryPipelineOptions()
                        }
                        buildMonitorView("${realm}-deploy-to-prod-monitor") {
                            jobs {
                                regex("^.*${realm}-deploy-to-prod\$")
                            }
                        }
                    }
                }
            }
        }
    }
}
