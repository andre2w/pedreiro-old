package com.github.andre2w.pedreiro.io

import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

class FileSystemHandler {
    fun createFolder(folderPath: String) {
        Files.createDirectory(folderPath.toPath())
    }

    fun createFile(filePath: String, fileContent: String) {
        Files.write(filePath.toPath(), fileContent.toByteArray())
    }

    fun readFile(filepath: String) : String? {
        return try {
            Files.readAllLines(filepath.toPath()).joinToString(System.lineSeparator())
        } catch (err: NoSuchFileException) {
            null
        }
    }

    private fun String.toPath() = Paths.get(this)
}
