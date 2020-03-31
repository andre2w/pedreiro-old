package com.github.andre2w.pedreiro

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

        val readString = Files.readString(path)
        assertThat(readString).isEqualTo(fileContent)

        Files.delete(path)
    }

    @Test
    internal fun `read file contents`() {
        Files.writeString(path, fileContent)

        val readContent = fileSystemHandler.readFile(filePath)

        assertThat(readContent).isEqualTo(fileContent)
        Files.delete(path)
    }

    @Test
    internal fun `create folder`() {
        val folderPath = "${System.getProperty("user.dir")}/test-folder-${Instant.now()}"

        fileSystemHandler.createFolder(folderPath)

        assertThat(Files.exists(Paths.get(folderPath))).isTrue()
        Files.delete(Paths.get(folderPath))
    }
}