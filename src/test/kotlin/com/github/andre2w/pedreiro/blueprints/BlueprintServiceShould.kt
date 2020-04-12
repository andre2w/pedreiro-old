package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.arguments.Arguments
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BlueprintServiceShould {

    private val blueprintReader = mockk<BlueprintReader>()
    private val blueprintService = BlueprintService(blueprintReader)

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
        every { blueprintReader.read(arguments) } returns blueprint

        val loadedTasks = blueprintService.loadBlueprint(arguments)

        val tasks = Blueprint.from(
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
        val arguments = Arguments(blueprintName)
        every { blueprintReader.read(arguments) } returns blueprint


        val loadedTasks = blueprintService.loadBlueprint(arguments)

        val tasks = Blueprint.from(
            CreateFolder("project"),
            CreateFile("project/build.gradle", "dependencies list")
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
        every { blueprintReader.read(arguments) } returns blueprint


        val loadedTasks = blueprintService.loadBlueprint(arguments)

        val tasks = Blueprint.from(
            CreateFolder("test-command"),
            ExecuteCommand("gradle init", "test-command")
        )

        assertThat(loadedTasks).isEqualTo(tasks)
    }

    @Test
    fun `throw exception when template is not valid`() {
        val blueprintName = "blueprintWithCommand"
        val blueprint = "\"INVALID:\":\":ASDF:"
        val arguments = Arguments(blueprintName)
        every { blueprintReader.read(arguments) } returns blueprint

        assertThrows<BlueprintParsingException> { blueprintService.loadBlueprint(arguments) }
    }


}

