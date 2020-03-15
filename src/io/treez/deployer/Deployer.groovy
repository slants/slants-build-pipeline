package io.treez.deployer

abstract class Deployer {

    def getGitVars() {
        def vars = [];

        vars.GIT_URL     = sh(script: "git config --get remote.origin.url",           returnStdout: true);
        vars.GIT_COMMIT  = sh(script: "git rev-parse HEAD",                           returnStdout: true);
        vars.GIT_AUTHOR  = sh(script: "git log -n 1 ${vars.GIT_COMMIT} --format=%aN", returnStdout: true);
        vars.GIT_MESSAGE = sh(script: "git log -1 --format=%B ${vars.GIT_COMMIT}",    returnStdout: true);

        return vars;
    }

}
