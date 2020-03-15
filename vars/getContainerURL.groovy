import io.treez.Docker

def call(build) {
    return Docker.getContainerURL(this, build)
}
