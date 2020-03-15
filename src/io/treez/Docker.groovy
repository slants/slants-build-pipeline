package io.treez

class Docker {
    static def findPort(script) {
        def dockerfile = script.readFile "Dockerfile"
        def lines = dockerfile.split("\n")

        def portLine = dockerfile.split("\n").find {
            return it.trim().startsWith('EXPOSE ')
        }

        if (portLine) {
            return portLine.trim().split().last().toInteger()
        }
        else {
            throw new Exception('Could not determine port for Dockerfile. Do you have an EXPOSE line?');
        }
    }

    static def getContainerURL(script, build, options = []) {
        def dockerImageName = Docker.getDockerImageName(script, build)

        def production = options.get('production', false)
        def registry = production ? '247579228338.dkr.ecr.us-west-2.amazonaws.com'
                                  : '228276746220.dkr.ecr.us-west-2.amazonaws.com'

        return "${registry}/${dockerImageName}"
    }

    static def getDockerImageName(script, build) {
        def tag = Docker.getDockerTag(script, build)

        return "${build.app.name}/${tag}".toLowerCase()
    }

    static def getDockerTag(script, build) {
        def branchName = Shared.getCleanedBranchName(build.commit.branch)
        def port       = Docker.findPort(script)

        return "${branchName}-${build.build.number}-${build.build.date}-${build.commit.short}-${port}".toLowerCase()
    }
}
