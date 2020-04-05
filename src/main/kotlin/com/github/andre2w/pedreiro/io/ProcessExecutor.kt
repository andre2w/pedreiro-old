package com.github.andre2w.pedreiro.io

import java.io.File

class ProcessExecutor {
    fun execute(command: String, runFolder: String) : Int {
        val process = ProcessBuilder()
            .command(command.split(" "))
            .directory(File(runFolder))
            .start()

        process.waitFor()

        return process.exitValue()
    }
}
