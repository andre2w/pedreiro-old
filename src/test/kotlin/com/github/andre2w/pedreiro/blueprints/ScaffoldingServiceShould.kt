package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.blueprints.CreateFile
import com.github.andre2w.pedreiro.blueprints.CreateFolder
import com.github.andre2w.pedreiro.blueprints.ScaffoldingService
import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class ScaffoldingServiceShould {

    @Test
    internal fun `create a folder in current location`() {
        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<Environment>()
        val baseDir = "/home/user/projects"
        every { environment.currentDir() } returns baseDir

        val scaffoldingService =
            ScaffoldingService(fileSystemHandler, environment)
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

    @Test
    internal fun `create file with contents in the current folder`() {
        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<Environment>()
        val baseDir = "/home/user/projects"
        every { environment.currentDir() } returns baseDir

        val scaffoldingService =
            ScaffoldingService(fileSystemHandler, environment)
        val tasks = listOf(
            CreateFolder("pedreiro"),
            CreateFile(
                "pedreiro/build.gradle",
                "dependencies"
            )
        )
        scaffoldingService.executeTasks(tasks)

        verify {
            fileSystemHandler.createFolder("/home/user/projects/pedreiro")
            fileSystemHandler.createFile("/home/user/projects/pedreiro/build.gradle", "dependencies")
        }
    }
}