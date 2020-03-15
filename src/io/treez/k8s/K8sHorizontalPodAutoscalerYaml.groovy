package io.treez.k8s

class K8sHorizontalPodAutoscalerYaml {

    static def getHorizontalPodAutoscalerConfig(Map options) {

        def defaultOptions = [
            maxReplicas: 9,
            minReplicas: 3,
            targetCPU:   70,
        ]

        def actualOptions = defaultOptions << options

        return [
            apiVersion: 'autoscaling/v1',
            kind:       'HorizontalPodAutoscaler',
            metadata:   [
                name:      "${actualOptions.app}-hpa",
                namespace: actualOptions.namespace,
            ],
            spec: [
                maxReplicas: actualOptions.maxReplicas,
                minReplicas: actualOptions.minReplicas,
                scaleTargetRef: [
                    apiVersion: 'apps/v1',
                    kind:       'Deployment',
                    name:       "${actualOptions.app}-deployment",
                ],
                targetCPUUtilizationPercentage: defaultOptions.targetCPU,
            ],
        ]
    }

}
