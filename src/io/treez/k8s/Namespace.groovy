package io.treez.k8s

class Namespace {

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
