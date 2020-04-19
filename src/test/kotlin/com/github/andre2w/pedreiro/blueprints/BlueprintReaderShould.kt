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

    private val parsedTemplate = """
        ---
        - type: folder
          name: "test"
          children:
            - type: command
              command: gradle init
    """.trimIndent()

    private val blueprintReader = BlueprintReader(fileSystemHandler, configuration, consoleHandler)

    @Test
    fun `read yaml blueprint from file system parsing variables`() {
        val arguments = Arguments("test", mapOf("project_name" to "test"))
        val filepath = "/home/user/pedreiro/.pedreiro/blueprints/test"
        every { fileSystemHandler.readFile("$filepath.yml") } returns blueprintTemplate
        every { fileSystemHandler.isFolder(filepath) } returns false

        val blueprint = blueprintReader.read(arguments)

        assertThat(blueprint).isEqualTo(Blueprint(parsedTemplate))
    }

    @Test
    fun `throw exception in case blueprint is not found`() {
        val filepath = "/home/user/pedreiro/.pedreiro/blueprints/test"
        every { fileSystemHandler.readFile("$filepath.yml") } returns null
        every { fileSystemHandler.readFile("$filepath.yaml") } returns null
        every { fileSystemHandler.isFolder(filepath) } returns false


        assertThrows<BlueprintParsingException> {
            blueprintReader.read(Arguments("test"))
        }
    }

    @Test
    internal fun `read blueprint with yaml extension`() {
        val arguments = Arguments("test", mapOf("project_name" to "test"))
        val filepath = "/home/user/pedreiro/.pedreiro/blueprints/test"
        every { fileSystemHandler.readFile("$filepath.yml")} returns null
        every { fileSystemHandler.readFile("$filepath.yaml") } returns parsedTemplate
        every { fileSystemHandler.isFolder(filepath) } returns false

        val blueprint = blueprintReader.read(arguments)

        assertThat(blueprint).isEqualTo(Blueprint(parsedTemplate))
    }

    @Test
    internal fun `read blueprint and other files in the folder`() {
        val template = """
            - type: file
              name: build.gradle
              source: build.gradle
        """.trimIndent()
        val buildGradle = """
            plugin {
              id 'kotlin'
            }
        """.trimIndent()
        val arguments = Arguments("test")
        val filepath = "/home/user/pedreiro/.pedreiro/blueprints/test"
        every { fileSystemHandler.isFolder(filepath) } returns true
        every { fileSystemHandler.readFile("$filepath/blueprint.yml") } returns template
        every { fileSystemHandler.readFile("$filepath/build.gradle") } returns buildGradle
        every { fileSystemHandler.listFilesIn(filepath) } returns listOf("blueprint.yml", "build.gradle")

        val blueprint = blueprintReader.read(arguments)

        val expectedBlueprint = Blueprint(template, mapOf("build.gradle" to buildGradle))
        assertThat(blueprint).isEqualTo(expectedBlueprint)
    }

    @Test
    internal fun `substitute variables in extra files`() {
        val template = """
            - type: file
              name: build.gradle
              source: build.gradle
        """.trimIndent()
        val buildGradleTemplate = """
            plugin {
              id 'kotlin' version: {{ kotlin_version }}
            }
        """.trimIndent()
        val buildGradle = """
            plugin {
              id 'kotlin' version: 1.3.71
            }
        """.trimIndent()
        val arguments = Arguments("test", mapOf("kotlin_version" to "1.3.71"))
        val filepath = "/home/user/pedreiro/.pedreiro/blueprints/test"
        every { fileSystemHandler.isFolder(filepath) } returns true
        every { fileSystemHandler.readFile("$filepath/blueprint.yml") } returns template
        every { fileSystemHandler.readFile("$filepath/build.gradle") } returns buildGradleTemplate
        every { fileSystemHandler.listFilesIn(filepath) } returns listOf("blueprint.yml", "build.gradle")

        val blueprint = blueprintReader.read(arguments)

        val expectedBlueprint = Blueprint(template, mapOf("build.gradle" to buildGradle))
        assertThat(blueprint).isEqualTo(expectedBlueprint)
    }

    @Test
    internal fun `throw exception when fail to load extra file`() {
        val template = """
            - type: file
              name: build.gradle
              source: build.gradle
        """.trimIndent()
        val buildGradleTemplate = """
            plugin {
              id 'kotlin' version: {{ kotlin_version }}
            }
        """.trimIndent()
        val arguments = Arguments("test", mapOf("kotlin_version" to "1.3.71"))
        val filepath = "/home/user/pedreiro/.pedreiro/blueprints/test"

        every { fileSystemHandler.isFolder(filepath) } returns true
        every { fileSystemHandler.readFile("$filepath/blueprint.yml") } returns template
        every { fileSystemHandler.readFile("$filepath/build.gradle") } returns null
        every { fileSystemHandler.listFilesIn(filepath) } returns listOf("blueprint.yml", "build.gradle")

        assertThrows<BlueprintParsingException> {
            blueprintReader.read(arguments)
        }

    }

    @Test
    internal fun `read variables file and use the values when reading templates`() {
        val template = """
            - type: file
              name: build.gradle
              source: build.gradle
        """.trimIndent()
        val buildGradleTemplate = """
            plugin {
              id 'kotlin' version: {{ kotlin_version }}
            }
        """.trimIndent()
        val buildGradle = """
            plugin {
              id 'kotlin' version: 1.3.71
            }
        """.trimIndent()
        val variables = """
            kotlin_version: 1.3.71
        """.trimIndent()
        val arguments = Arguments("test")
        val filepath = "/home/user/pedreiro/.pedreiro/blueprints/test"
        every { fileSystemHandler.isFolder(filepath) } returns true
        every { fileSystemHandler.readFile("$filepath/blueprint.yml") } returns template
        every { fileSystemHandler.readFile("$filepath/build.gradle") } returns buildGradleTemplate
        every { fileSystemHandler.readFile("$filepath/variables.yml") } returns variables
        every { fileSystemHandler.listFilesIn(filepath) } returns listOf("blueprint.yml", "build.gradle", "variables.yml")

        val blueprint = blueprintReader.read(arguments)

        val expectedBlueprint = Blueprint(template, mapOf("build.gradle" to buildGradle))
        assertThat(blueprint).isEqualTo(expectedBlueprint)
    }
}