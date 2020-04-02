package com.github.andre2w.pedreiro

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class BuildServiceShould {

    @Test
    fun `get tasks from template and execute them`() {
        val scaffoldingService = mockk<ScaffoldingService>(relaxed = true)
        val templateService = mockk<TemplateService>(relaxed = true)
        val tasks = listOf(
            CreateFolder("project"),
            CreateFolder("project/src"),
            CreateFolder("project/src/main"),
            CreateFolder("project/src/test")
        )
        val templateName = "baseTemplate"
        val args = Arguments(templateName)
        every { templateService.loadTemplate(templateName) } returns tasks

        val buildService = BuildService(templateService, scaffoldingService)
        buildService.build(args)

        verify {
            scaffoldingService.executeTasks(tasks)
        }
    }
}