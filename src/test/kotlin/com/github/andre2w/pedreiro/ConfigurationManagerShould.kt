package com.github.andre2w.pedreiro

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConfigurationManagerShould {

    @Test
    internal fun `retrieve configurations from the file system`() {
        val blueprintsFolder = "/home/pedreiro/.pedreiro/blueprints"
        val configFilePath = "/home/pedreiro/.pedreiro/config.yaml"
        val configurationFile = """
            blueprintsFolder: "$blueprintsFolder"
        """.trimIndent()
        val configuration = PedreiroConfiguration(blueprintsFolder)
        val fileSystemHandler = mockk<FileSystemHandler>()
        every { fileSystemHandler.readFile(configFilePath) } returns configurationFile

        val configurationManager = ConfigurationManager(fileSystemHandler)
        val loadedConfiguration : PedreiroConfiguration = configurationManager.loadConfiguration(configFilePath)

        assertThat(loadedConfiguration).isEqualTo(configuration)
    }
}