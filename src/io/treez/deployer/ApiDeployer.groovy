package io.treez.deployer

//class ApiDeployer extends Deployer {

    def deploy(Map args) {

        def params = [
                DEWEY: "Tran",
                YUP: args.refuse
        ]

        // create build.json
        // create deploy files
        // deploy

        deployViaK8s(params);

    }

    def deployViaK8s(Map params) {

        println "Deploying via K8s"
        println "param: $params.DEWEY"
        println "param: $params.YUP"

    }

//}
