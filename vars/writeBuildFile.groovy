def call() {

    def name = env.GIT_URL.replaceFirst(/^.*\/([^\/]+?).git$/, '$1')
    def now  = new Date()

    def build = [
        app: [
            name:    name,
            type:    name.tokenize('-').last().toLowerCase()
        ],
        build: [
            date:      now.format('yyyyMMdd'),
            millis:    now.getTime(),
            number:    env.BUILD_NUMBER,
            timestamp: now.toString()
        ],
        commit: [
            author:  sh(script: "git log -n 1 ${env.GIT_COMMIT} --format=%aN", returnStdout: true).trim(),
            branch:  env.BRANCH_NAME,
            hash:    env.GIT_COMMIT,
            message: sh(script: "git log -1 --format=%B ${env.GIT_COMMIT}", returnStdout: true).trim(),
            short:   env.GIT_COMMIT.take(7),
            url:     env.GIT_URL.replaceAll('.git$', "/commit/${env.GIT_COMMIT}")
        ]
    ]

    def buildJson = groovy.json.JsonOutput.toJson(build)
    buildJson = groovy.json.JsonOutput.prettyPrint(buildJson)
    writeFile file: 'build.json', text: buildJson

    echo buildJson

}
