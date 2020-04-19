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

    fun read(arguments: Arguments): Blueprint {
        val blueprintPath = "${configuration.blueprintsFolder}/${arguments.blueprintName}"

        if (fileSystemHandler.isFolder(blueprintPath)) {

            val blueprintTemplate = fileSystemHandler.readFile("$blueprintPath/blueprint.yml")
                ?: throw BlueprintParsingException("Failed to read blueprint ${arguments.blueprintName}")

            val blueprint = parseTemplate(blueprintTemplate, arguments)

            val extraFiles = fileSystemHandler.listFilesIn(blueprintPath)

            val files = HashMap<String, String>()

            for (extraFile in extraFiles) {
                if (extraFile != "blueprint.yml") {
                    files[extraFile] = fileSystemHandler.readFile("$blueprintPath/$extraFile")!!
                }
            }

            return Blueprint(blueprint, files)
        } else {
            val blueprintTemplate = readFile("$blueprintPath.yml")
                ?: readFile("$blueprintPath.yaml")
                ?: throw BlueprintParsingException("Failed to read blueprint ${arguments.blueprintName}")

            val blueprint = parseTemplate(blueprintTemplate, arguments)

            return Blueprint(blueprint)
        }
    }

    private fun parseTemplate(blueprintTemplate: String, arguments: Arguments) : String =
        handlebars
            .compileInline(blueprintTemplate)
            .apply(arguments.extraArgs)

    private fun readFile(blueprintPath: String) : String? {
        val blueprint = fileSystemHandler.readFile(blueprintPath)
        if (blueprint != null) {
            consoleHandler.print("Creating project from blueprint ($blueprintPath)")
        }
        return blueprint
    }

}
