package com.github.andre2w.pedreiro.blueprints

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.andre2w.pedreiro.arguments.Arguments
import com.github.andre2w.pedreiro.configuration.PedreiroConfiguration
import com.github.andre2w.pedreiro.io.ConsoleHandler
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.YAMLParser
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
            Blueprint(loadFromFile(blueprintPath, arguments))
        }
    }

    private fun loadFromFile(blueprintPath: String, arguments: Arguments): String {
        val blueprintTemplate = readFile("$blueprintPath.yml")
            ?: readFile("$blueprintPath.yaml")
            ?: throw BlueprintParsingException("Failed to read blueprint ${arguments.blueprintName}")

        return parseTemplate(blueprintTemplate, arguments)
    }

    private fun loadFromFolder(blueprintPath: String, arguments: Arguments): Blueprint {
        val variablesFile = loadFromFile("$blueprintPath/variables", arguments)

        val variables = YAMLParser.objectMapper.readValue<Map<String, String>>(variablesFile)
        val mergedArguments = arguments.mergeWith(variables)

        val blueprint = loadFromFile("$blueprintPath/blueprint", mergedArguments)
        val extraFiles = loadExtraFiles(blueprintPath, mergedArguments)

        return Blueprint(blueprint, extraFiles)
    }

    private fun loadExtraFiles(blueprintPath: String, arguments: Arguments): Map<String, String> {
        val extraFiles = fileSystemHandler.listFilesIn(blueprintPath)

        return extraFiles.asSequence()
            .filter { file -> file != "blueprint.yml" && file != "variables.yml"}
            .map { file -> readExtraFile(blueprintPath, file, arguments) }
            .toMap()
    }

    private fun readExtraFile(blueprintPath: String, file: String, arguments: Arguments): Pair<String, String> {
        val extraFileTemplate = fileSystemHandler.readFile("$blueprintPath/$file")
            ?: throw BlueprintParsingException("Failed to read file $file")

        val extraFile = parseTemplate(extraFileTemplate, arguments)

        return Pair(file, extraFile)
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
