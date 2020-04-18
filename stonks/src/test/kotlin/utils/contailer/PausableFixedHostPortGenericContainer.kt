package utils.contailer

import org.testcontainers.containers.FixedHostPortGenericContainer

class PausableFixedHostPortGenericContainer(imageName: String) :
    FixedHostPortGenericContainer<PausableFixedHostPortGenericContainer>(imageName) {
    fun pauseContainer() {
        dockerClient.pauseContainerCmd(getContainerId()).exec()
    }

    fun resumeContainer() {
        dockerClient.unpauseContainerCmd(getContainerId()).exec()
    }
}
