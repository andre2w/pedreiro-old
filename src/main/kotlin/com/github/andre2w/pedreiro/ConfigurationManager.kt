package com.github.andre2w.pedreiro

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.lang.IllegalArgumentException

class ConfigurationManager(private val fileSystemHandler: FileSystemHandler) {
    fun loadConfiguration(configFilePath: String): PedreiroConfiguration {
        val yaml = Yaml()

        val configuration = fileSystemHandler.readFile(configFilePath)
        val parsedConfiguration = yaml.load<Any>(configuration)

        return PedreiroConfiguration((parsedConfiguration as Map<String, String>)["templatesFolder"]!!)
    }

}
