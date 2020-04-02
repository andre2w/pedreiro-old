package com.github.andre2w.pedreiro

class Main {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            Pedreiro(FileSystemHandler(), Environment()).execute(args)
        }
    }
}
