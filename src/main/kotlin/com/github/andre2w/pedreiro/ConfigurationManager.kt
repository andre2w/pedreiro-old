package com.github.andre2w.pedreiro

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

class ConfigurationManager(private val fileSystemHandler: FileSystemHandler) {

    private val objectMapper  by lazy {
        ObjectMapper(YAMLFactory()).registerModule(KotlinModule())
    }

    fun loadConfiguration(configFilePath: String): PedreiroConfiguration {
        val configuration = fileSystemHandler.readFile(configFilePath)

        return objectMapper.readValue(configuration)
    }

}
