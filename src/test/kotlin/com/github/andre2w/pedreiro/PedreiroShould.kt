package com.github.andre2w.pedreiro

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class PedreiroShould {

    @Test
    fun `main application should get tasks from template and execute them`() {
        val scaffoldingService = mockk<ScaffoldingService>(relaxed = true)
        val templateService = mockk<TemplateService>(relaxed = true)
        val argumentParser = mockk<ArgumentParser>()
        val tasks = listOf(
            CreateFolder("project"),
            CreateFolder("project/src"),
            CreateFolder("project/src/main"),
            CreateFolder("project/src/test")
        )
        val templateName = "baseTemplate"
        val args = Arguments(templateName)
        every { templateService.loadTemplate(templateName) } returns tasks
        every { argumentParser.parse(arrayOf(templateName)) } returns args

        val pedreiro = Pedreiro(scaffoldingService, templateService, argumentParser)
        pedreiro.execute(arrayOf(templateName))

        verify {
            scaffoldingService.executeTasks(tasks)
        }
    }
}