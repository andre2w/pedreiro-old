package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.arguments.Arguments
import com.github.andre2w.pedreiro.blueprints.BlueprintService
import com.github.andre2w.pedreiro.blueprints.BuildService
import com.github.andre2w.pedreiro.blueprints.CreateFolder
import com.github.andre2w.pedreiro.blueprints.ScaffoldingService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class BuildServiceShould {

    @Test
    fun `get tasks from blueprint and execute them`() {
        val scaffoldingService = mockk<ScaffoldingService>(relaxed = true)
        val blueprintService = mockk<BlueprintService>(relaxed = true)
        val tasks = listOf(
            CreateFolder("project"),
            CreateFolder("project/src"),
            CreateFolder("project/src/main"),
            CreateFolder("project/src/test")
        )
        val blueprintName = "baseTemplate"
        val args = Arguments(blueprintName)
        every { blueprintService.loadBlueprint(blueprintName) } returns tasks

        val buildService =
            BuildService(blueprintService, scaffoldingService)
        buildService.build(args)

        verify {
            scaffoldingService.executeTasks(tasks)
        }
    }
}