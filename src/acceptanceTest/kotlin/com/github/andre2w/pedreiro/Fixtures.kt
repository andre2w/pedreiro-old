package com.github.andre2w.pedreiro


class Fixtures {

    companion object {

        val CONFIGURATION = """
            ---
            templatesFolder: "/home/user/pedreiro/.pedreiro/templates"
        """.trimIndent()

        val BUILD_GRADLE_CONTENT = """
          plugins {
            id 'org.jetbrains.kotlin.jvm' version '1.3.71'
          }
          group 'org.example'
          version '1.0-SNAPSHOT'""".trimIndent()

        val SIMPLE_TEMPLATE = """
        ---
        - type: folder
          name: test
          children:
            - type: folder
              name: src
              children:
                - type: folder
                  name: main
                  children:
                    - type: folder
                      name: kotlin
                    - type: folder
                      name: resources
                - type: file
                  name: build.gradle
                  content: |-
                    plugins {
                      id 'org.jetbrains.kotlin.jvm' version '1.3.71'
                    }
                    group 'org.example'
                    version '1.0-SNAPSHOT'""".trimIndent()
    }
}