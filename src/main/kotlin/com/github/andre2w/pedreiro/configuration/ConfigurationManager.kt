package com.github.andre2w.pedreiro.configuration

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.YAMLParser

class ConfigurationManager(private val fileSystemHandler: FileSystemHandler) {

    private val objectMapper = YAMLParser.objectMapper

    fun loadConfiguration(configFilePath: String): PedreiroConfiguration {
        val configuration = fileSystemHandler.readFile(configFilePath) ?: throw ConfigurationNotFound(configFilePath)

        return objectMapper.readValue(configuration)
    }

}
