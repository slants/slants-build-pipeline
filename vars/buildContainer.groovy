import io.treez.Docker

def call() {
    def build = getBuild()

    buildContainer(build)
}

def buildContainer(build) {
    def tag  = Docker.getDockerTag(this, build)
    def name = Docker.getDockerImageName(this, build)

    build.image = [
        name: name,
        tag:  tag,
    ]

    def buildJson = groovy.json.JsonOutput.toJson(build)
    writeFile file: 'build.json', text: groovy.json.JsonOutput.prettyPrint(buildJson)

    ////////

    sh "docker build . --build-arg npmToken=${env.NPM_TREEZ_TOKEN} --build-arg dockerTag=${name} -t ${name}"
}
