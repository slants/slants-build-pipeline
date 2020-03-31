package io.treez.k8s

class Deployment {

    static def getConfig(Map options = [:], script) {

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
}
