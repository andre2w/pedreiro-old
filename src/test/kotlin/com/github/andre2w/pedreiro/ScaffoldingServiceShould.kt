package com.github.andre2w.pedreiro

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class ScaffoldingServiceShould {

    @Test
    internal fun `create a folder in current location for CreateFolder task`() {
        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<Environment>()
        val baseDir = "/home/user/projects"
        every { environment.currentDir() } returns baseDir

        val scaffoldingService = ScaffoldingService(fileSystemHandler, environment)
        val tasks = listOf(
            CreateFolder("pedreiro"),
            CreateFolder("pedreiro/src")
        )
        scaffoldingService.executeTasks(tasks)

        verify {
            fileSystemHandler.createFolder("/home/user/projects/pedreiro")
            fileSystemHandler.createFolder("/home/user/projects/pedreiro/src")
        }
    }
}