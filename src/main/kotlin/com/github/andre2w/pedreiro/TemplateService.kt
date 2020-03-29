package com.github.andre2w.pedreiro

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.Node

class TemplateService(
    private val configuration: PedreiroConfiguration,
    private val fileSystemHandler: FileSystemHandler
) {
    fun loadTemplate(templateName: String) : List<CreateFolder> {
        val template = fileSystemHandler.readFile("${configuration.templatesFolder}/${templateName}.yml")
        val yaml = Yaml()
        val yamlTemplate = yaml.load<List<Map<String,Any>>>(template)

        val tasks = parseTemplate(yamlTemplate)

        return tasks
    }

    private fun parseTemplate(yamlTemplate: List<Map<String, Any>>): ArrayList<CreateFolder> {
        val tasks = ArrayList<CreateFolder>()

        for (entry in yamlTemplate) {
            tasks.addAll(parseEntry(entry, ""))
        }
        return tasks
    }

    private fun parseEntry(entry: Map<String, Any>, level: String): List<CreateFolder> {

        val result = ArrayList<CreateFolder>()

        val path = if (level.isEmpty())
            entry["name"].toString()
        else
            level + "/" + entry["name"]

        result.add( CreateFolder(path) )

        if (entry.containsKey("children") && entry["children"] != null) {
            for (child in entry["children"] as List<Map<String,Any>>) {
                result.addAll( parseEntry(child, path) )
            }
        }

        return result
    }
}
