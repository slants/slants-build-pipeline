import io.treez.Docker
import io.treez.Shared

def call() {
    def build = getBuild()

    def appType = build.app.type;

    if (appType == 'api') {
        buildContainer()
        publishContainer()
    }
    else if(appType == 'ui') {
        archiveToS3(build)
    }
    else {
        throw new Error("Unsupported app type '${appType}'")
    }
}

def getFileName(build) {
    def branchName = Shared.getCleanedBranchName(build.commit.branch)

    return "${build.app.name}.${branchName}-${build.build.number}-${build.build.date}-${build.commit.short}.zip"
}

def getS3URL(bucket, name, branch, filename) {
    return "s3://${bucket}/${name}/${branch}/${filename}"
}

def archiveToS3(build) {
    def branch   = Shared.getCleanedBranchName(build.commit.branch)
    def bucket   = 'build-treez-ui-builds'
    def filename = getFileName(build)
    def url      = getS3URL(bucket, build.app.name, branch, filename)


    build.archive = [
        bucket: bucket,
        name:   filename,
        url:    url,
    ]

    def buildJson = groovy.json.JsonOutput.toJson(build)
    writeFile file: 'build.json', text: groovy.json.JsonOutput.prettyPrint(buildJson)

    ////////

    sh "cp build.json ./dist/"

    zip archive: true, dir: 'dist', zipFile: "${filename}"

    sh "aws s3 cp ${filename} ${url}"

}
