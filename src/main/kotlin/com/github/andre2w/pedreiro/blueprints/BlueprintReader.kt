package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.arguments.Arguments
import com.github.andre2w.pedreiro.configuration.PedreiroConfiguration
import com.github.andre2w.pedreiro.io.ConsoleHandler
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.jknack.handlebars.Handlebars

class BlueprintReader(
    private val fileSystemHandler: FileSystemHandler,
    private val configuration: PedreiroConfiguration,
    private val consoleHandler: ConsoleHandler
) {

    private val handlebars = Handlebars()

    fun read(arguments: Arguments): String {
        val blueprintPath = "${configuration.blueprintsFolder}/${arguments.blueprintName}"

        val blueprintTemplate = fileSystemHandler.readFile("$blueprintPath.yml")
            ?: fileSystemHandler.readFile("$blueprintPath.yaml")
            ?: throw BlueprintParsingException("Failed to read blueprint ${arguments.blueprintName} ($blueprintPath)")

        consoleHandler.print("Creating project from blueprint ($blueprintPath)")

        return handlebars
            .compileInline(blueprintTemplate)
            .apply(arguments.extraArgs)
    }

}
