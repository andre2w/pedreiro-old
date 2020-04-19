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

        return if (fileSystemHandler.isFolder(blueprintPath)) {
            loadFromFolder(blueprintPath, arguments)
        } else {
            loadFromFile(blueprintPath, arguments)
        }
    }

    private fun loadFromFile(blueprintPath: String, arguments: Arguments): Blueprint {
        val blueprintTemplate = readFile("$blueprintPath.yml")
            ?: readFile("$blueprintPath.yaml")
            ?: throw BlueprintParsingException("Failed to read blueprint ${arguments.blueprintName}")

        val blueprint = parseTemplate(blueprintTemplate, arguments)

        return Blueprint(blueprint)
    }

    private fun loadFromFolder(
        blueprintPath: String,
        arguments: Arguments
    ): Blueprint {
        val blueprintTemplate = fileSystemHandler.readFile("$blueprintPath/blueprint.yml")
            ?: throw BlueprintParsingException("Failed to read blueprint ${arguments.blueprintName}")

        val blueprint = parseTemplate(blueprintTemplate, arguments)
        val files = loadExtraFiles(blueprintPath, arguments)

        return Blueprint(blueprint, files)
    }

    private fun loadExtraFiles(blueprintPath: String, arguments: Arguments): HashMap<String, String> {
        val extraFiles = fileSystemHandler.listFilesIn(blueprintPath)
        val files = HashMap<String, String>()

        for (extraFile in extraFiles) {
            if (extraFile != "blueprint.yml") {
                val extraFileTemplate = fileSystemHandler.readFile("$blueprintPath/$extraFile")!!
                val parsedExtraFile = parseTemplate(extraFileTemplate, arguments)
                files[extraFile] = parsedExtraFile
            }
        }
        return files
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
