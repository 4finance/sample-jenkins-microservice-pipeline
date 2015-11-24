package io.fourfinanceit.pipeline.example.common

/**
 * @author Tomasz Uliński
 * @author Łukasz Szczęsny
 */
class Rundeck {

    public static final String DEPLOY_SERVICE_TASK = 'deploy:deploy'

    private static final String RUNDECK_NOTIFIER = 'org.jenkinsci.plugins.rundeck.RundeckNotifier'

    static Closure invoke(String task, Map<String, String> rundeckOptions) {
        return {
            rundeck(task) {
                options rundeckOptions
                shouldWaitForRundeckJob()
                shouldFailTheBuild()
            }

            publisherNodes.find {
                it.name() == RUNDECK_NOTIFIER
            }.append(NodeBuilder.newInstance().includeRundeckLogs(true))
        }
   }
    
}
