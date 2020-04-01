package com.github.andre2w.pedreiro

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConfigurationManagerShould {

    @Test
    internal fun `retrieve configurations from the file system`() {
        val templatesFolder = "/home/pedreiro/.pedreiro/templates"
        val configFilePath = "/home/pedreiro/.pedreiro/config.yaml"
        val configurationFile = """
            templatesFolder: "$templatesFolder"
        """.trimIndent()
        val configuration = PedreiroConfiguration(templatesFolder)
        val fileSystemHandler = mockk<FileSystemHandler>()
        every { fileSystemHandler.readFile(configFilePath) } returns configurationFile

        val configurationManager = ConfigurationManager(fileSystemHandler)
        val loadedConfiguration : PedreiroConfiguration = configurationManager.loadConfiguration(configFilePath)

        assertThat(loadedConfiguration).isEqualTo(configuration)
    }
}