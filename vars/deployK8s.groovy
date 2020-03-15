/**
 * Requires an enclosing ssh-agent to work without passwords.
 *
 * @param options Named params:
 *
 * app: Name of application
 */

import io.treez.K8s

def call(Map options) {
    writeSSHHostKeys()

    def production = options.get('production', false)

    def host = production ? 'kubectl.prod.treez.io' :
                            'kubectl.int.treez.io'
    def path = K8s.getConfigPath()

    sh "ssh ec2-user@${host} 'mkdir -p ${options.app}'"
    sh "scp -r ${path}/* ec2-user@${host}:${options.app}/"
    sh "ssh ec2-user@${host} 'kubectl apply -R -f ${options.app}'"
}

/**
 * Keep the expected hostkey here to prevent MITM attacks. If our bastion host changes, or we have a
 * fleet of them, we'll need to update this method as well.
 */
def writeSSHHostKeys() {
    def buildTreezHostKey = 'kubectl.int.treez.io,10.240.32.127 ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBPIjxJ2n39Hn15PSBBwP2c5vJDD57K/BPLvWfkHGiGWsg1MSQb53kq0Vt2pQHlc4AA+tpgqw6wqVSuK0inwf88I='
    def prodTreezHostKey  = 'kubectl.prod.treez.io,10.120.77.61 ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBHsImgYEBWYvA7oMtKHB03KhTwyVaqpT04d3gaYelXQ4L7Z9Nib0LAaUdxKjx/JPQx5eHQD+qcZChNCs6aGCQ+4='

    sh "mkdir -p ~/.ssh"
    sh "echo '${buildTreezHostKey}' >> ~/.ssh/known_hosts"
    sh "echo '${prodTreezHostKey}' >> ~/.ssh/known_hosts"
}
