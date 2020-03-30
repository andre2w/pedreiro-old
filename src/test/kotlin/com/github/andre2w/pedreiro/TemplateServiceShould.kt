package com.github.andre2w.pedreiro

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TemplateServiceShould {

    @Test
    fun `parse template that only create folders`() {
        val templateName = "templateName"
        val template = """- type: folder
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
              name: resources""".trimIndent()
        val fileSystemHandler = mockk<FileSystemHandler>()
        val configuration = PedreiroConfiguration("/home/user/.pedreiro")
        every { fileSystemHandler.readFile("/home/user/.pedreiro/${templateName}.yml") } returns template

        val templateService = TemplateService(configuration, fileSystemHandler)

        val loadedTasks = templateService.loadTemplate(templateName)

        val tasks = listOf(
            CreateFolder("project"),
            CreateFolder("project/src"),
            CreateFolder("project/src/main"),
            CreateFolder("project/src/main/kotlin"),
            CreateFolder("project/src/main/resources"))

        assertThat(loadedTasks).isEqualTo(tasks)
    }

    @Test
    internal fun `parse template that has text files`() {
        val templateName = "templateName"
        val template = """- type: folder
  name: project 
  children:
    - type: file
      name: build.gradle
      content: dependencies list""".trimIndent()

        val fileSystemHandler = mockk<FileSystemHandler>()
        val configuration = PedreiroConfiguration("/home/user/.pedreiro")
        every { fileSystemHandler.readFile("/home/user/.pedreiro/${templateName}.yml") } returns template

        val templateService = TemplateService(configuration, fileSystemHandler)

        val loadedTasks = templateService.loadTemplate(templateName)

        val tasks = listOf(
            CreateFolder("project"),
            CreateFile("project/build.gradle", "dependencies list")
        )

        assertThat(loadedTasks).isEqualTo(tasks)
    }
}