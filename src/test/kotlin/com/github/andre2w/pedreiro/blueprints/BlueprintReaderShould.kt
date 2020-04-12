package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.arguments.Arguments
import com.github.andre2w.pedreiro.configuration.PedreiroConfiguration
import com.github.andre2w.pedreiro.io.ConsoleHandler
import com.github.andre2w.pedreiro.io.FileSystemHandler
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BlueprintReaderShould {

    private val fileSystemHandler = mockk<FileSystemHandler>()
    private val configuration = PedreiroConfiguration("/home/user/pedreiro/.pedreiro/blueprints")
    private val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)
    private val blueprintTemplate = """
        ---
        - type: folder
          name: "{{ project_name }}"
          children:
            - type: command
              command: gradle init
    """.trimIndent()

    private val expectedBlueprint = """
        ---
        - type: folder
          name: "test"
          children:
            - type: command
              command: gradle init
    """.trimIndent()


    @Test
    fun `read yaml blueprint from file system parsing variables`() {
        val arguments = Arguments("test", mapOf("project_name" to "test"))
        every { fileSystemHandler.readFile("/home/user/pedreiro/.pedreiro/blueprints/test.yml") } returns blueprintTemplate

        val blueprintReader = BlueprintReader(fileSystemHandler, configuration, consoleHandler)
        val blueprint = blueprintReader.read(arguments)

        assertThat(blueprint).isEqualTo(expectedBlueprint)
    }

    @Test
    fun `throw exception in case blueprint is not found`() {
        every { fileSystemHandler.readFile("/home/user/pedreiro/.pedreiro/blueprints/test.yml") } returns null

        val blueprintReader = BlueprintReader(fileSystemHandler, configuration, consoleHandler)

        assertThrows<BlueprintParsingException> {
            blueprintReader.read(Arguments("test"))
        }
    }



}