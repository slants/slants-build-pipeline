package io.treez.deployer

class ApiDeployer extends Deployer {

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

    private def deployViaK8s(Map params) {

        echo "Deploying via K8s"
        echo "param: $params.DEWEY"
        echo "param: $params.YUP"

    }

}
