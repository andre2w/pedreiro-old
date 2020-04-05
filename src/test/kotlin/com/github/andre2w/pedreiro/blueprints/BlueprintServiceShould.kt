package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.configuration.PedreiroConfiguration
import com.github.andre2w.pedreiro.io.ConsoleHandler
import com.github.andre2w.pedreiro.io.FileSystemHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BlueprintServiceShould {

    private val fileSystemHandler = mockk<FileSystemHandler>()
    private val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)
    private val configuration = PedreiroConfiguration("/home/user/.pedreiro")
    private val blueprintService = BlueprintService(configuration, fileSystemHandler, consoleHandler)

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
        every { fileSystemHandler.readFile("/home/user/.pedreiro/${blueprintName}.yml") } returns blueprint

        val loadedTasks = blueprintService.loadBlueprint(blueprintName)

        val tasks = listOf(
            CreateFolder("project"),
            CreateFolder("project/src"),
            CreateFolder("project/src/main"),
            CreateFolder("project/src/main/kotlin"),
            CreateFolder("project/src/main/resources")
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
        every { fileSystemHandler.readFile("/home/user/.pedreiro/${blueprintName}.yml") } returns blueprint

        val loadedTasks = blueprintService.loadBlueprint(blueprintName)

        val tasks = listOf(
            CreateFolder("project"),
            CreateFile(
                "project/build.gradle",
                "dependencies list"
            )
        )

        assertThat(loadedTasks).isEqualTo(tasks)
    }

    @Test
    fun `print message with loaded blueprint`() {
        val blueprintName = "blueprintName"
        val blueprint = """
          - type: folder
            name: project
          """.trimIndent()
        val blueprintPath = "/home/user/.pedreiro/${blueprintName}.yml"
        every { fileSystemHandler.readFile(blueprintPath) } returns blueprint

        blueprintService.loadBlueprint(blueprintName)

        verify {
            consoleHandler.print("Creating project from blueprint ($blueprintPath)")
        }
    }

    @Test
    internal fun `parse blueprint with command`() {
        val blueprintName = "blueprintWithCommand"
        val blueprint = """
            ---
            - type: folder
              name: test-command
              children:
                - type: command
                  command: gradle init
        """.trimIndent()
        every { fileSystemHandler.readFile("/home/user/.pedreiro/${blueprintName}.yml")} returns blueprint

        val loadedTasks = blueprintService.loadBlueprint(blueprintName)

        val tasks = listOf(
            CreateFolder("test-command"),
            ExecuteCommand("gradle init", "test-command")
        )
        assertThat(loadedTasks).isEqualTo(tasks)
    }

    @Test
    fun `throw exception when template is not found`() {
        val blueprintName = "invalidBlueprint"
        every { fileSystemHandler.readFile("/home/user/.pedreiro/${blueprintName}.yml")} returns null

        assertThrows<BlueprintNotFound> { blueprintService.loadBlueprint(blueprintName) }
    }
}

