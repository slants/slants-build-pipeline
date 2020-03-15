import io.treez.Docker

def call(buildJson = null, env = "ci") {

    def build = (buildJson != null) ? buildJson : readJSON(file: "build.json")

    if (build.app.type == "api") {
        def appEnv   = "${build.app.name}-${env}"
        def imageUrl = Docker.getContainerURL(this, build)
        def nodeEnv  = env

        api(appEnv, imageUrl)
    }
    else if(build.app.type == "ui") {
        def subdomain  = build.app.name.replaceAll('-treez-ui$', '')
        def hostname   = "${subdomain}-${env}.treez.io"
        def archiveUrl = build.archive.url
        def nodeEnv    = env

        ui(hostname, archiveUrl)
    }

}

def api(appEnv, imageUrl) {

    def env = appEnv.split('-').last()
    def app = appEnv.replaceAll("-${env}\$", '')

    def image      = imageUrl.split('/').last()
    def imageParts = image.split('-')
    def port       = imageParts[imageParts.length - 1]

    def awsAccessKeyId     = sh(script: "aws ssm get-parameters --region us-west-2 --names /apps/aws/access_key_id --query Parameters[0].Value --with-decryption | sed 's/\"//g'", returnStdout: true).trim()
    def awsSecretAccessKey = sh(script: "aws ssm get-parameters --region us-west-2 --names /apps/aws/secret_access_key --query Parameters[0].Value --with-decryption | sed 's/\"//g'", returnStdout: true).trim()

    writeFile file: '.ebignore', text: """
        *
        !Dockerrun.aws.json
        !.ebextensions/*
    """

    writeFile file: '.ebextensions/environment.config', text: """
        {
            "option_settings": [
                {
                    "option_name": "NODE_ENV",
                    "value": "${env}"
                },
                {
                    "option_name": "AWS_ACCESS_KEY_ID",
                    "value": "${awsAccessKeyId}"
                },
                {
                    "option_name": "AWS_SECRET_ACCESS_KEY",
                    "value": "${awsSecretAccessKey}"
                }
            ]
        }
    """

    writeFile file: '.elasticbeanstalk/config.yml', text: """
        branch-defaults:
            default:
                environment: ${appEnv}
        environment-defaults:
            ${appEnv}:
                branch: null
                repository: null
        global:
            application_name: ${app}
            default_ec2_keyname: null
            default_platform: arn:aws:elasticbeanstalk:us-west-2::platform/Docker
                running on 64bit Amazon Linux/2.12.17
            default_region: us-west-2
            include_git_submodules: true
            instance_profile: null
            platform_name: null
            platform_version: null
            sc: null
            workspace_type: Application
    """

    writeFile file: 'Dockerrun.aws.json', text: """
        {
            "AWSEBDockerrunVersion": "1",
            "Image": {
                "Name": "${imageUrl}",
                "Update": "true"
            },
            "Ports": [
                {
                    "ContainerPort": "${port}"
                }
            ]
        }
    """

    sh """
       set +x
       eval \$(aws ecr get-login --registry-ids 228276746220  --no-include-email --region us-west-2 | sed 's|https://||')
       set -x
       """

    sh "docker run -e NODE_ENV=${env} -t ${imageUrl} npm run migrate --if-present"

    sh "eb deploy --debug --label ${image}-${env}"

}

def ui(hostname, archiveUrl) {
    dir('workspace') {

        def filename = archiveUrl.tokenize('/').last()

        // download
        sh "aws s3 cp ${archiveUrl} ${filename}"

        // extract
        sh "unzip ${filename}"
        sh "rm ${filename}"

        // upload
        sh "aws s3 sync . s3://${hostname} --delete"

    }
}
