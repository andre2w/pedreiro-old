package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.ProcessExecutor
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScaffoldingServiceShould {

    private val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
    private val environment = mockk<Environment>()
    private val processExecutor = mockk<ProcessExecutor>()

    private val scaffoldingService = ScaffoldingService(fileSystemHandler, environment, processExecutor, CommandParser())

    private val baseDir = "/home/user/projects"

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `create a folder in current location`() {
        every { environment.currentDir() } returns baseDir

        val blueprint = Tasks.from(
            CreateFolder("pedreiro", fileSystemHandler, environment),
            CreateFolder("pedreiro/src", fileSystemHandler, environment)
        )
        scaffoldingService.execute(blueprint)

        verify {
            fileSystemHandler.createFolder("/home/user/projects/pedreiro")
            fileSystemHandler.createFolder("/home/user/projects/pedreiro/src")
        }
    }

    @Test
    fun `create file with contents in the current folder`() {
        every { environment.currentDir() } returns baseDir
        val tasks = Tasks.from(
            CreateFolder("pedreiro", fileSystemHandler, environment),
            CreateFile("pedreiro/build.gradle", "dependencies")
        )


        scaffoldingService.execute(tasks)

        verify {
            fileSystemHandler.createFolder("/home/user/projects/pedreiro")
            fileSystemHandler.createFile("/home/user/projects/pedreiro/build.gradle", "dependencies")
        }
    }

    @Test
    fun `execute command in the specified folder`() {
        val command = listOf("gradle", "init")
        val tasks = Tasks.from(ExecuteCommand("gradle init", "pedreiro"))

        every { environment.currentDir() } returns baseDir
        every { processExecutor.execute(command, "/home/user/projects/pedreiro") } returns 0

        scaffoldingService.execute(tasks)

        verify { processExecutor.execute(command, "/home/user/projects/pedreiro") }
    }


}