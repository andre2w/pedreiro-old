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
        val blueprintTemplate = readBlueprint(blueprintPath, arguments.blueprintName)

        val blueprint = handlebars
            .compileInline(blueprintTemplate)
            .apply(arguments.extraArgs)

        consoleHandler.print("Creating project from blueprint ($blueprintPath)")

        val blueprintTree = try {
            objectMapper.readTree(blueprint)
        } catch (err: JacksonYAMLParseException) {
            throw BlueprintParsingException("Failed to load blueprint ${arguments.blueprintName} ($blueprintPath)")
        }

        return parseBlueprint(blueprintTree)
    }

    private fun readBlueprint(blueprintPath: String, blueprintName: String): String =
        fileSystemHandler.readFile(blueprintPath)
            ?: throw BlueprintParsingException("Failed to load blueprint $blueprintName ($blueprintPath)")

    private fun parseBlueprint(node: JsonNode) : List<Task> {
        val result = ArrayList<Task>()
        when {
            node.isArray -> node.forEach { node -> result.addAll(parseBlueprint(node)) }
            else -> {
                when (val parsedEntry = parseEntry("", node)) {
                    is ParseResult.Many -> result.addAll(parsedEntry.tasks)
                    is ParseResult.Single -> result.add(parsedEntry.task)
                }
            }
        }
        return result
    }

    private fun parseEntry(level: String, node: JsonNode): ParseResult {
        return when (node["type"].asText()) {
            "command" -> parseCommand(node, level)
            "file"    -> parseCreateFile(normalizePath(level, node), node)
            "folder"  -> parseCreateFolder(level, node)
            else -> throw BlueprintParsingException("Invalid type of ${node["type"].asText()}")
        }
    }

    private fun parseCreateFolder(level: String, node: JsonNode) : ParseResult.Many {
        val result = ArrayList<Task>()

        result.add(CreateFolder(normalizePath(level, node)))

        node["children"]?.forEach { child ->
            when (val parseResult = parseEntry(normalizePath(level, node), child)) {
                is ParseResult.Single -> result.add(parseResult.task)
                is ParseResult.Many -> result.addAll(parseResult.tasks)
            }
        }

        return ParseResult.Many(result)
    }

    private fun normalizePath(level: String, node: JsonNode): String {
        return if (level.isEmpty()) node.get("name").asText() else level + "/" + node.get("name").asText()
    }

    private fun parseCreateFile(path: String, node: JsonNode): ParseResult.Single {
        return ParseResult.Single(CreateFile(path, node["content"].asText()))
    }

    private fun parseCommand(node: JsonNode, path: String): ParseResult.Single {
        return ParseResult.Single(ExecuteCommand(node["command"].asText(), path))
    }

}
