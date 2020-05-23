package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.arguments.Arguments
import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.ProcessExecutor
import com.github.andre2w.pedreiro.tasks.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BlueprintServiceShould {

    private val blueprintReader = mockk<BlueprintReader>()
    private val fileSystemHandler = mockk<FileSystemHandler>()
    private val environment = mockk<Environment>()
    private val processExecutor = mockk<ProcessExecutor>()
    private val commandParser = mockk<CommandParser>()
    private val blueprintService = BlueprintService(blueprintReader, fileSystemHandler, environment, processExecutor, commandParser)

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `parse blueprint that only create folders`() {
        val blueprintName = "blueprintName"
        val blueprint = """
        - type: folder
          name: project 
          children:
            - type: folder
              name: src
              children:
                 - type: folder
                   name: main
                   children:
                     - type: folder
                       name: kotlin
                     - type: folder
                       name: resources
        """.trimIndent()
        val arguments = Arguments(blueprintName)
        every { blueprintReader.read(arguments) } returns Blueprint(blueprint)

        val loadedTasks = blueprintService.loadBlueprint(arguments)

        val tasks = Tasks.from(
            CreateFolder(
                "project",
                fileSystemHandler,
                environment
            ),
            CreateFolder(
                "project/src",
                fileSystemHandler,
                environment
            ),
            CreateFolder(
                "project/src/main",
                fileSystemHandler,
                environment
            ),
            CreateFolder(
                "project/src/main/kotlin",
                fileSystemHandler,
                environment
            ),
            CreateFolder(
                "project/src/main/resources",
                fileSystemHandler,
                environment
            )
        )

        assertThat(loadedTasks).isEqualTo(tasks)
    }

    @Test
    fun `parse blueprint that has text files`() {
        val blueprintName = "blueprintName"
        val blueprint = """
        - type: folder
          name: project 
          children:
            - type: file
              name: build.gradle
              content: dependencies list""".trimIndent()
        val arguments = Arguments(blueprintName)
        every { blueprintReader.read(arguments) } returns Blueprint(blueprint)


        val loadedTasks = blueprintService.loadBlueprint(arguments)

        val tasks = Tasks.from(
            CreateFolder(
                "project",
                fileSystemHandler,
                environment
            ),
            CreateFile(
                "project/build.gradle",
                "dependencies list",
                fileSystemHandler,
                environment
            )
        )

        assertThat(loadedTasks).isEqualTo(tasks)
    }

    @Test
    fun `parse blueprint with command`() {
        val blueprintName = "blueprintWithCommand"
        val blueprint = """
            ---
            - type: folder
              name: test-command
              children:
                - type: command
                  command: gradle init
        """.trimIndent()
        val arguments = Arguments(blueprintName)
        every { blueprintReader.read(arguments) } returns Blueprint(blueprint)


        val loadedTasks = blueprintService.loadBlueprint(arguments)

        val tasks = Tasks.from(
            CreateFolder(
                "test-command",
                fileSystemHandler,
                environment
            ),
            ExecuteCommand(
                "gradle init",
                "test-command",
                processExecutor,
                commandParser,
                environment
            )
        )

        assertThat(loadedTasks).isEqualTo(tasks)
    }

    @Test
    fun `throw exception when template is not valid`() {
        val blueprintName = "blueprintWithCommand"
        val blueprint = "\"INVALID:\":\":ASDF:"
        val arguments = Arguments(blueprintName)
        every { blueprintReader.read(arguments) } returns Blueprint(blueprint)

        assertThrows<BlueprintParsingException> { blueprintService.loadBlueprint(arguments) }
    }

    @Test
    internal fun `parse CreateFile using content from file in folder`() {
        val blueprint = """
            - type: file
              name: build.gradle
              source: build.gradle
        """.trimIndent()
        val arguments = Arguments("multifile-blueprint")
        val files = mapOf(
            "build.gradle" to "id 'kotlin'"
        )
        every { blueprintReader.read(arguments) } returns Blueprint(blueprint, files)

        val loadedTasks = blueprintService.loadBlueprint(arguments)

        val expectedTasks = Tasks.from(
            CreateFile(
                "build.gradle",
                "id 'kotlin'",
                fileSystemHandler,
                environment
            )
        )
        assertThat(loadedTasks).isEqualTo(expectedTasks)
    }
}

