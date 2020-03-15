/**
 * Writes a set of k8s config files appropriate for default settings for a
 * NodeJS app. This currently includes deployment, service, HPA, ingress,
 * and namespace configs. :options are passed directly to the underlying
 * configuration methods, so refer to the docs for those methods to see
 * what you can pass.
 *
 * @param options Named parameter map that's the union of all dependent options
 */

def call(Map options) {
    /**
     * Rewrite `app` to incorporate `nodeEnv` so we don't have colliding
     * configuration filenames.
     *
     * Unfortunately, Jenkins CPS doesn't support groovy spread operators, so
     * imperative go go go.
     */
    def actualOptions = options.clone()
    actualOptions.app = "${options.app}-${options.nodeEnv}"

    writeK8sDeploymentYaml(actualOptions)
    writeK8sHorizontalPodAutoscalerYaml(actualOptions)
    writeK8sIngressYaml(actualOptions)
    writeK8sNamespaceYaml(actualOptions)
    writeK8sServiceYaml(actualOptions)
}
