package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class CreateFolderShould {

    @Test
    internal fun `create folder in the specified path`() {
        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<Environment>()
        every { environment.currentDir() } returns "/home/andre/projects"

        val createFolder = CreateFolder("test-folder", fileSystemHandler, environment)
        createFolder.execute()

        verify {
            fileSystemHandler.createFolder("/home/andre/projects/test-folder")
        }
    }
}