package io.treez.k8s

class Service {

    static def getConfig(Map options) {
        return [
            apiVersion: 'v1',
            kind:       'Service',
            metadata:   [
                name:      "${options.app}-service",
                namespace: options.namespace,
            ],
            spec: [
                selector: [
                    app: options.app,
                ],
                type: 'NodePort',
                ports: [
                    [
                        name:       'https',
                        port:       443,
                        protocol:   'TCP',
                        targetPort: Docker.findPort(this),
                    ],
                ],
            ],
        ]
    }

}
