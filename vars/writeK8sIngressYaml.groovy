/**
 * Writes an ingress.yaml file for k8s.
 *
 * @param options Named parameter map that includes the following:
 *
 * app:       application name (no spaces)
 * domain:    domain for DNS
 * hostname:  hostname (without domain name)
 * namespace: cluster namespace
 */
import io.treez.K8s

def call(Map options) {
    def ingressConfig = getIngressConfig(options);

    K8s.writeConfig this, file: "/${options.app}/ingress.yaml", data: ingressConfig
}

def getIngressConfig(Map options) {
    return [
        apiVersion: 'extensions/v1beta1',
        kind:       'Ingress',
        metadata:   [
            annotations: [
                'kubernetes.io/ingress.class':                    'alb',
                'alb.ingress.kubernetes.io/scheme':               'internet-facing',
                'external-dns.alpha.kubernetes.io/hostname':      options.domain,
                'alb.ingress.kubernetes.io/healthcheck-path':     '/health-check-ping',
                'alb.ingress.kubernetes.io/listen-ports':         '[{"HTTP": 80}, {"HTTPS":443}]',
                'alb.ingress.kubernetes.io/certificate-arn':      'arn:aws:acm:us-west-2:228276746220:certificate/82dd6279-8bbd-45df-823c-c7ac24858050',
                'alb.ingress.kubernetes.io/actions.ssl-redirect': '{"Type": "redirect", "RedirectConfig": { "Protocol": "HTTPS", "Port": "443", "StatusCode": "HTTP_301"}}',
            ],
            labels: [
                app: options.app,
            ],
            name:      "${options.app}-ingress",
            namespace: options.namespace,
        ],
        spec: [
            rules: [
                [
                    host: "${options.hostname}.${options.domain}",
                    http: [
                        paths: [
                            [
                                path: '/*',
                                backend: [
                                    serviceName: 'ssl-redirect',
                                    servicePort: 'use-annotation',
                                ],
                            ],
                            [
                                path: '/*',
                                backend: [
                                    serviceName: "${options.app}-service",
                                    servicePort: 443,
                                ],
                            ],
                        ],
                    ],
                ],
            ],
        ],
    ]
}
