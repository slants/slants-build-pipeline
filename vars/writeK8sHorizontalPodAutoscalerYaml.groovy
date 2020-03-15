/**
 * Writes a horizontalPodAutoscaler.yaml file for k8s.
 *
 * @param options Named parameter map that includes the following:
 *
 * app:         application name (no spaces)
 * maxReplicas: maximum number of replicas
 * minReplicas: minimum number of replicas
 * namespace:   cluster namespace
 * targetCPU:   target CPU utilization before new replicas are spun up
 */
import io.treez.K8s

def call(Map options) {
    def horizontalPodAutoscalerConfig = getHorizontalPodAutoscalerConfig(options);

    K8s.writeConfig this, file: "/${options.app}/horizontalPodAutoscaler.yaml", data: horizontalPodAutoscalerConfig
}

def getHorizontalPodAutoscalerConfig(Map options) {
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
