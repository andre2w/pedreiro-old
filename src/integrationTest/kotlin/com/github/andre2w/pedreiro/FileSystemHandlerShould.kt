package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.io.FileSystemHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

class FileSystemHandlerShould {

    private val fileContent = """
        ---
        - name: project
          type: folder
          children:
            - name: build.gradle
              type: file
              content: |
                apply plugin: kotlin
    """.trimIndent()

    private val filePath = "${System.getProperty("user.dir")}/temptestfile.txt"
    private val path = Paths.get(filePath)
    private val fileSystemHandler = FileSystemHandler()

    @Test
    fun `create file with contents`() {
        fileSystemHandler.createFile(filePath, fileContent)

        val readString = String(Files.readAllBytes(path))
        assertThat(readString).isEqualTo(fileContent)

        Files.delete(path)
    }

    @Test
    fun `read file contents`() {
        Files.write(path, fileContent.toByteArray())

        val readContent = fileSystemHandler.readFile(filePath)

        assertThat(readContent).isEqualTo(fileContent)
        Files.delete(path)
    }

    @Test
    fun `create folder`() {
        val folderPath = "${System.getProperty("user.dir")}/test-folder-${Instant.now()}"

        fileSystemHandler.createFolder(folderPath)

        assertThat(Files.exists(Paths.get(folderPath))).isTrue()
        Files.delete(Paths.get(folderPath))
    }

    @Test
    fun `return null when file is not found`() {
        val readContent = fileSystemHandler.readFile(filePath)

        assertThat(readContent).isNull()
    }

    @Test
    internal fun `check if path is a folder`() {
        val folderPath = "${System.getProperty("user.dir")}/test-folder-${Instant.now()}"
        Files.createDirectory(Paths.get(folderPath))

        assertThat(fileSystemHandler.isFolder(folderPath)).isTrue()

        Files.delete(Paths.get(folderPath))
    }

    @Test
    internal fun `list file names inside a folder`() {
        val folderPath = "${System.getProperty("user.dir")}/test-folder-${Instant.now()}"
        Files.createDirectory(Paths.get(folderPath))
        Files.write(Paths.get("$folderPath/test.txt"), fileContent.toByteArray())

        val filesInFolder = fileSystemHandler.listFilesIn(folderPath)

        assertThat(filesInFolder).isEqualTo(listOf("test.txt"))

        Files.delete(Paths.get("$folderPath/test.txt"))
        Files.delete(Paths.get(folderPath))
    }
}