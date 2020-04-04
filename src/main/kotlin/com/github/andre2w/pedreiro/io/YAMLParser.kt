package com.github.andre2w.pedreiro.io

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule

object YAMLParser {

    val objectMapper: ObjectMapper = ObjectMapper(YAMLFactory())
        .registerModule(KotlinModule())


}