package io.treez.k8s

class K8sNamespaceYaml {

    def getConfig(Map options) {
        return [
            apiVersion: 'v1',
            kind:       'Namespace',
            metadata:   [
                name: options.namespace,
            ],
        ]
    }

}
