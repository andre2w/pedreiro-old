package com.github.andre2w.pedreiro

class Main {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val fileSystemHandler = FileSystemHandler()
            Pedreiro(
                ScaffoldingService(fileSystemHandler, Environment()),
                TemplateService( PedreiroConfiguration(""), fileSystemHandler ),
                ArgumentParser()
            ).execute(args)
        }
    }
}
