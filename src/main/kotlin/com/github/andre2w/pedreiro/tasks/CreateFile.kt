package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler

data class CreateFile(
    val path: String,
    val content: String,
    private val fileSystemHandler: FileSystemHandler,
    private val environment: Environment
) : Task {
    override fun execute() {
        fileSystemHandler.createFile("${environment.currentDir()}/$path",content)
    }
}