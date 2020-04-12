package com.github.andre2w.pedreiro.blueprints

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.yaml.JacksonYAMLParseException
import com.github.andre2w.pedreiro.arguments.Arguments
import com.github.andre2w.pedreiro.configuration.PedreiroConfiguration
import com.github.andre2w.pedreiro.io.ConsoleHandler
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.YAMLParser
import com.github.jknack.handlebars.Handlebars

class BlueprintService(
    private val configuration: PedreiroConfiguration,
    private val fileSystemHandler: FileSystemHandler,
    private val consoleHandler: ConsoleHandler
) {

    sealed class ParseResult {
        data class Single(val task: Task) : ParseResult()
        data class Many(val tasks: List<Task>) : ParseResult()
    }

    private val handlebars = Handlebars()

    private val objectMapper = YAMLParser.objectMapper

    fun loadBlueprint(arguments: Arguments) : List<Task> {
        val blueprintPath = "${configuration.blueprintsFolder}/${arguments.blueprintName}.yml"
        val blueprint = readBlueprint(blueprintPath, arguments)

        consoleHandler.print("Creating project from blueprint ($blueprintPath)")

        val blueprintTree = try {
            objectMapper.readTree(blueprint)
        } catch (err: JacksonYAMLParseException) {
            throw BlueprintParsingException("Failed to load blueprint ${arguments.blueprintName} ($blueprintPath)")
        }

        return parseBlueprint(blueprintTree)
    }

    private fun readBlueprint(blueprintPath: String, arguments: Arguments): String {
        val blueprintTemplate = fileSystemHandler.readFile(blueprintPath)
            ?: throw BlueprintParsingException("Failed to load blueprint ${arguments.blueprintName} ($blueprintPath)")

        return handlebars
            .compileInline(blueprintTemplate)
            .apply(arguments.extraArgs)
    }

    private fun parseBlueprint(node: JsonNode, level: List<String> = ArrayList()) : List<Task> {
        if (node.isArray) {
            return parseList(node, level)
        }

        val result = ArrayList<Task>()
        val parsedResult =  when (node["type"].asText()) {
            "command" -> parseCommand(level.asPath(), node)
            "file"    -> parseCreateFile(level.asPath() , node)
            "folder"  -> parseCreateFolder(level, node)
            else -> throw BlueprintParsingException("Invalid type of ${node["type"].asText()}")
        }

        when (parsedResult) {
            is ParseResult.Many -> result.addAll(parsedResult.tasks)
            is ParseResult.Single -> result.add(parsedResult.task)
        }

        return result
    }

    private fun parseList(nodes: JsonNode, level: List<String>): List<Task> =
        nodes.flatMap { node -> parseBlueprint(node, level) }

    private fun parseCreateFolder(level: List<String>, node: JsonNode) : ParseResult.Many {
        val result = ArrayList<Task>()

        val currentLevel = level + node["name"].asText()

        result.add(CreateFolder(currentLevel.asPath()))

        node["children"]?.let { children ->
            result.addAll(parseList(children, currentLevel))
        }

        return ParseResult.Many(result)
    }

    private fun parseCreateFile(path: String, node: JsonNode): ParseResult.Single {
        return ParseResult.Single(CreateFile(path + "/" + node["name"].asText(), node["content"].asText()))
    }

    private fun parseCommand(path: String, node: JsonNode): ParseResult.Single {
        return ParseResult.Single(ExecuteCommand(node["command"].asText(), path))
    }

    private fun List<String>.asPath() = this.joinToString("/")
}
