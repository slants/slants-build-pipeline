/**
 * Writes a service.yaml file for k8s. Don't forget your deployment.yaml!
 *
 * @param options Named parameter map that includes the following:
 *
 * app:       application name (no spaces)
 * namespace: cluster namespace
 */

import io.treez.Docker
import io.treez.K8s

def call(Map options) {
    def serviceConfig = getServiceConfig(options);

    K8s.writeConfig this, file: "/${options.app}/service.yaml", data: serviceConfig
}

def getServiceConfig(Map options) {
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
