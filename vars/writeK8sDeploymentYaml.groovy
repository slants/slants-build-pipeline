/**
 * Writes a deployment.yaml file for k8s. Don't forget your service.yaml!
 *
 * @param options Named parameter map that includes the following:
 *
 * app:           application name (no spaces)
 * cpuLimit:      Max cpu limit (e.g. "1000m")
 * cpuRequest:    Initial cpu request (e.g. "1000m")
 * image:         Docker image (e.g. ECS url + tag)
 * memoryLimit:   Max memory limit (e.g. "1000M")
 * memoryRequest: Initial memory request (e.g. "1000M")
 * namespace:     cluster namespace
 * nodeEnv:       for Node applications, which NODE_ENV to use for this pod
 */

import io.treez.Docker
import io.treez.K8s

def call(Map options) {
    def deploymentConfig = getDeploymentConfig(options, this);

    K8s.writeConfig this, file: "/${options.app}/deployment.yaml", data: deploymentConfig
}

def getDeploymentConfig(Map options = [:], script) {
    def defaultOptions = [
        cpuLimit:      "1000m",
        cpuRequest:    "500m",
        image:         script.getContainerURL(script.getBuild()),
        memoryLimit:   "2000M",
        memoryRequest: "256M",
        nodeEnv:       'staging',
    ]

    def actualOptions = defaultOptions << options

    return [
        apiVersion: 'apps/v1',
        kind:       'Deployment',
        metadata:   [
            labels: [
                app: actualOptions.app,
            ],
            name:      "${actualOptions.app}-deployment",
            namespace: actualOptions.namespace,
        ],
        spec: [
            replicas: 3,
            selector: [
                matchLabels: [
                    app: actualOptions.app
                ],
            ],
            template: [
                metadata: [
                    labels: [
                        app: actualOptions.app,
                    ]
                ],
                spec: [
                    containers: [
                        [
                            name:  actualOptions.app,
                            image: actualOptions.image,
                            ports: [
                                [
                                    containerPort: Docker.findPort(this),
                                    name:          'service-port',
                                ],
                            ],
                            env: [
                                [
                                    name:  'NODE_ENV',
                                    value: actualOptions.nodeEnv,
                                ],
                            ],
                            resources: [
                                limits: [
                                    cpu:    actualOptions.cpuLimit,
                                    memory: actualOptions.memoryLimit,
                                ],
                                requests: [
                                    cpu:    actualOptions.cpuRequest,
                                    memory: actualOptions.memoryRequest,
                                ],
                            ],
                        ],
                    ],
                ],
            ],
        ],
    ]
}
