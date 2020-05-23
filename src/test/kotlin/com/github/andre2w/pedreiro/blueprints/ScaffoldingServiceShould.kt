package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.ProcessExecutor
import com.github.andre2w.pedreiro.tasks.*
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScaffoldingServiceShould {

    private val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
    private val environment = mockk<Environment>()
    private val processExecutor = mockk<ProcessExecutor>()
    private val commandParser = CommandParser()

    private val scaffoldingService = ScaffoldingService()

    private val baseDir = "/home/user/projects"

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `execute all tasks`() {
        every { environment.currentDir() } returns baseDir
        val createFolder = mockk<CreateFolder>(relaxUnitFun = true)
        val createFile = mockk<CreateFile>(relaxUnitFun = true)

        val blueprint = Tasks.from(
            createFolder,
            createFile)

        scaffoldingService.execute(blueprint)

        verifyOrder {
            createFolder.execute()
            createFile.execute()
        }
    }
}