package io.treez.k8s

class Ingress {

    static def getConfig(Map options) {
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

}
