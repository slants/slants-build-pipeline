/**
 * Writes a namespace.yaml file for k8s.
 *
 * @param options Named parameter map that includes the following:
 *
 * namespace: cluster namespace
 */
import io.treez.K8s

def call(Map options) {
    def namespaceConfig = getNamespaceConfig(options);

    K8s.writeConfig this, file: "/${options.app}/namespace.yaml", data: namespaceConfig
}

def getNamespaceConfig(Map options) {
    return [
        apiVersion: 'v1',
        kind:       'Namespace',
        metadata:   [
            name:      options.namespace,
        ],
    ]
}
