package com.github.andre2w.pedreiro

import com.fasterxml.jackson.module.kotlin.readValue

class ConfigurationManager(private val fileSystemHandler: FileSystemHandler) {

    private val objectMapper = YAMLParser.objectMapper

    fun loadConfiguration(configFilePath: String): PedreiroConfiguration {
        val configuration = fileSystemHandler.readFile(configFilePath)

        return objectMapper.readValue(configuration)
    }

}
