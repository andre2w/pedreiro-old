package com.github.andre2w.pedreiro.blueprints

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.yaml.JacksonYAMLParseException
import com.github.andre2w.pedreiro.arguments.Arguments
import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.ProcessExecutor
import com.github.andre2w.pedreiro.io.YAMLParser

sealed class ParseResult {
    data class Single(val task: Task) : ParseResult()
    data class Many(val tasks: List<Task>) : ParseResult()
}

class BlueprintService(
    private val blueprintReader: BlueprintReader,
    private val fileSystemHandler: FileSystemHandler,
    private val environment: Environment,
    private val processExecutor: ProcessExecutor,
    private val commandParser: CommandParser
) {

    private val objectMapper = YAMLParser.objectMapper

    fun loadBlueprint(arguments: Arguments) : Tasks {

        val blueprint = blueprintReader.read(arguments)
        val blueprintTasks = try {
            objectMapper.readTree(blueprint.tasks)
        } catch (err: JacksonYAMLParseException) {
            throw BlueprintParsingException("Failed to parse blueprint ${arguments.blueprintName}")
        }

        return Tasks.from(parse(blueprintTasks, blueprint))
    }

    private fun parse(node: JsonNode, blueprint : Blueprint, level: List<String> = ArrayList()) : List<Task> {
        if (node.isArray) {
            return parseList(node, level, blueprint)
        }

        val result = ArrayList<Task>()
        val parsedResult =  when (node["type"].asText()) {
            "command" -> parseCommand(level.asPath(), node)
            "file"    -> parseCreateFile(level.asPath() , node, blueprint)
            "folder"  -> parseCreateFolder(level, node, blueprint)
            else -> throw BlueprintParsingException("Invalid type of ${node["type"].asText()}")
        }

        when (parsedResult) {
            is ParseResult.Many -> result.addAll(parsedResult.tasks)
            is ParseResult.Single -> result.add(parsedResult.task)
        }

        return result
    }

    private fun parseList(nodes: JsonNode, level: List<String>, blueprint: Blueprint): List<Task> =
        nodes.flatMap { node -> parse(node, blueprint,level) }

    private fun parseCreateFolder(level: List<String>, node: JsonNode, blueprint: Blueprint) : ParseResult.Many {
        val result = ArrayList<Task>()

        val currentLevel = level + node["name"].asText()

        result.add(CreateFolder(currentLevel.asPath(), fileSystemHandler, environment))

        node["children"]?.let { children ->
            result.addAll(parseList(children, currentLevel, blueprint))
        }

        return ParseResult.Many(result)
    }

    private fun parseCreateFile(path: String, node: JsonNode, blueprint: Blueprint): ParseResult.Single {

        val filePath = if (path == "") node["name"].asText() else path + "/" + node["name"].asText()

        val createFile = if (node.has("content")) {
            CreateFile(filePath, node["content"].asText(), fileSystemHandler, environment)
        } else {
            CreateFile(filePath, blueprint.fileContentOf(node["source"].asText()), fileSystemHandler, environment)
        }

        return ParseResult.Single(createFile)
    }

    private fun parseCommand(path: String, node: JsonNode): ParseResult.Single {
        return ParseResult.Single(ExecuteCommand(node["command"].asText(), path, processExecutor, commandParser))
    }

    private fun List<String>.asPath() = this.joinToString("/")
}
