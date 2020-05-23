package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class CreateFileShould {

    @Test
    internal fun `create file with contents`() {
        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<Environment>()
        val content = """
            plugins {
                id 'java'
            }
        """.trimIndent()
        every { environment.currentDir() } returns "/home/andre/project"

        val createFile = CreateFile(
            "test-project/build.gradle",
            content,
            fileSystemHandler,
            environment
        )
        createFile.execute()

        verify {
            fileSystemHandler.createFile("/home/andre/project/test-project/build.gradle", content)
        }
    }
}