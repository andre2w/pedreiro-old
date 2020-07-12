package com.github.andre2w.pedreiro.io

import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths
import java.util.stream.Collectors.toList

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

    fun isFolder(path: String) : Boolean {
        return Files.isDirectory(path.toPath())
    }

    fun listFilesIn(folderPath: String) : List<String> {
        val files = Files.list(folderPath.toPath())
        return files.map { file -> file.fileName.toString() }
            .collect(toList())
    }

    private fun String.toPath() = Paths.get(this).normalize()
}
