package com.github.andre2w.pedreiro.configuration

import com.github.andre2w.pedreiro.io.FileSystemHandler
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

    @Test
    internal fun `throw exception when configuration is not found`() {
        val configFilePath = "/home/pedreiro/.pedreiro/config.yaml"
        val fileSystemHandler = mockk<FileSystemHandler>()
        every { fileSystemHandler.readFile(configFilePath) } returns null

        val configurationManager = ConfigurationManager(fileSystemHandler)

        assertThrows<ConfigurationNotFound> {
           configurationManager.loadConfiguration(configFilePath)
        }
    }
}