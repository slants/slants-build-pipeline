package io.treez

class Shared {
    static def getCleanedBranchName(branchName) {
        return branchName.replaceAll('[^A-Za-z0-9]', '_')
    }
}
