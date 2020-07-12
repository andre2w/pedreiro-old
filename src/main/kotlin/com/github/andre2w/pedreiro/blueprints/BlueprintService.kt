package com.github.andre2w.pedreiro.blueprints

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.yaml.JacksonYAMLParseException
import com.github.andre2w.pedreiro.arguments.Arguments
import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.ProcessExecutor
import com.github.andre2w.pedreiro.io.YAMLParser
import com.github.andre2w.pedreiro.tasks.*
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.parser.ParserException

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

    fun loadBlueprint(arguments: Arguments): Tasks {

        val blueprint = blueprintReader.read(arguments)

        val yaml = Yaml()

        val blueprintTasks = try {
            yaml.load<Any>(blueprint.tasks)
        } catch (err: ParserException) {
            throw BlueprintParsingException("Failed to parse blueprint ${arguments.blueprintName}")
        }

        return Tasks.from(parse(SnakeYamlNode(blueprintTasks), blueprint))
    }

    private fun parse(
        yamlNode: YamlNode,
        blueprint: Blueprint,
        level: List<String> = ArrayList()
    ): List<Task> {

        if (yamlNode.isArray()) {
            return parseList(level, blueprint, yamlNode)
        }

        val result = ArrayList<Task>()
        val parsedResult = when (yamlNode.getTextFromField("type")) {
            "command" -> parseCommand(level.asPath(), yamlNode)
            "file" -> parseCreateFile(level.asPath(), blueprint, yamlNode)
            "folder" -> parseCreateFolder(level, blueprint, yamlNode)
            else -> throw BlueprintParsingException("Invalid type of ${yamlNode.getTextFromField("type")}")
        }

        when (parsedResult) {
            is ParseResult.Many -> result.addAll(parsedResult.tasks)
            is ParseResult.Single -> result.add(parsedResult.task)
        }

        return result
    }

    private fun parseList(
        level: List<String>,
        blueprint: Blueprint,
        yamlNode: YamlNode
    ): List<Task> {
        return yamlNode.flatMap { node -> parse(node, blueprint, level) }
    }

    private fun parseCreateFolder(
        level: List<String>,
        blueprint: Blueprint,
        yamlNode: YamlNode
    ): ParseResult.Many {
        val result = ArrayList<Task>()

        val currentLevel = level + yamlNode.getTextFromField("name")

        result.add(
            CreateFolder(
                currentLevel.asPath(),
                fileSystemHandler,
                environment
            )
        )

        yamlNode.getChildren("children")?.let { children ->
            result.addAll(parseList(currentLevel, blueprint, children))
        }

        return ParseResult.Many(result)
    }

    private fun parseCreateFile(
        path: String,
        blueprint: Blueprint,
        yamlNode: YamlNode
    ): ParseResult.Single {

        val filePath = if (path == "") {
            yamlNode.getTextFromField("name")
        } else {
            path + "/" + yamlNode.getTextFromField("name")
        }

        val content = if (yamlNode.hasField("content")) {
            yamlNode.getTextFromField("content")
        } else {
            blueprint.fileContentOf(yamlNode.getTextFromField("source"))
        }

        val createFile = CreateFile(
            filePath,
            content,
            fileSystemHandler,
            environment
        )

        return ParseResult.Single(createFile)
    }

    private fun parseCommand(path: String, yamlNode: YamlNode): ParseResult.Single {
        return ParseResult.Single(
            ExecuteCommand(
                yamlNode.getTextFromField("command"),
                path,
                processExecutor,
                commandParser,
                environment
            )
        )
    }

    private fun List<String>.asPath() = this.joinToString("/")
}

interface YamlNode : Iterable<YamlNode> {
    fun isArray(): Boolean
    fun getTextFromField(fieldName: String): String
    fun getChildren(fieldName: String): YamlNode?
    fun hasField(fieldName: String): Boolean
}

class JacksonYamlNode(private val jsonNode: JsonNode) : YamlNode, Iterable<YamlNode> {
    override fun isArray(): Boolean {
        return jsonNode.isArray
    }

    override fun getTextFromField(fieldName: String): String {
        return jsonNode[fieldName].asText()
    }

    override fun getChildren(fieldName: String): YamlNode? {
        return jsonNode[fieldName]?.let { JacksonYamlNode(it) }
    }

    override fun hasField(fieldName: String): Boolean {
        return jsonNode.has(fieldName)
    }

    override fun iterator(): Iterator<JacksonYamlNode> {
        return jsonNode.map { JacksonYamlNode(it) }.iterator()
    }
}


class SnakeYamlNode(private val node: Any) : YamlNode, Iterable<YamlNode> {
    override fun isArray(): Boolean {
        return node is List<*>
    }

    override fun getTextFromField(fieldName: String): String {
        return (node as Map<String, String>)[fieldName]!!
    }

    override fun getChildren(fieldName: String): YamlNode? {
        return (node as Map<*, *>)[fieldName]?.let { SnakeYamlNode(it) }
    }

    override fun hasField(fieldName: String): Boolean {
        return (node as Map<*, *>).containsKey(fieldName)
    }

    override fun iterator(): Iterator<YamlNode> {
        return (node as List<Any>)
            .map { SnakeYamlNode(it) }
            .iterator()
    }

}