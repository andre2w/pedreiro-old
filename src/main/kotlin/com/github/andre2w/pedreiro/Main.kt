package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.io.ConsoleHandler
import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler

class Main {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            Pedreiro(FileSystemHandler(),
                Environment(), ConsoleHandler()
            ).execute(args)
        }
    }
}
