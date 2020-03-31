package io.treez.deployer

def deploy(Map options = [:]) {

    def params = [
        DEWEY: "Tran",
        YUP: options.refuse
    ]

    // create build.json
    // create deploy files
    // deploy

    writeBuildJson(params);

    deployViaK8s(params);

    withCredentials(bindings: [sshUserPrivateKey(credentialsId: "jenkins-ssh", keyFileVariable: 'keyfile')]) {
        sh "ssh -o 'StrictHostKeyChecking no' -i ${keyfile} ec2-user@kubectl.build.treez.io 'ls'"
    }

}

def writeBuildJson(Map params) {

}

def deployViaK8s(Map params) {

    sh "echo Deploying via K8s"
    sh "echo param: $params.DEWEY"
    sh "echo param: $params.YUP"

}
