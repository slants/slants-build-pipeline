/**
 * Thin wrapper to enable Jenkins pipelines to fetch the same build info as
 * the groovy pipeline.
 */
def call() {
    return getBuild()
}

def getBuild() {
    def json = readJSON file: "build.json"

    return json
}
