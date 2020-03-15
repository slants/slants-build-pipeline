import io.treez.Docker
import io.treez.Shared

def call(Map options) {
    def build = getBuild()

    publishContainer(build, options)
}

def publishContainer(build, Map options) {
    def url = Docker.getContainerURL(this, build, options)

    def production = options.get('production', false)
    def accountId = production ? '247579228338' : '228276746220'

    sh """
       set +x
       eval \$(aws ecr get-login --no-include-email --region us-west-2 --registry-ids ${accountId} | sed 's|https://||')
       set -x
       """

    sh "docker tag ${build.image.name} ${url}"
    sh "docker push ${url}"
}
