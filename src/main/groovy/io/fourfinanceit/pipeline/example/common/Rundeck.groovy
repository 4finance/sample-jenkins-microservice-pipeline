package io.fourfinanceit.pipeline.example.common

/**
 * @author Tomasz Uliński
 * @author Łukasz Szczęsny
 */
class Rundeck {

    public static final String DEPLOY_SERVICE_TASK = 'deploy:deploy'

    static Closure invoke(String task, Map<String, String> rundeckOptions) {
        return {
            rundeck(task) {
                options rundeckOptions
                shouldWaitForRundeckJob()
                shouldFailTheBuild()
                includeRundeckLogs()
            }
        }
   }
}
