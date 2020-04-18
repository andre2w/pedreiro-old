package com.github.andre2w.pedreiro

object FolderFixtures {
    val BUILD_GRADLE_TEMPLATE = """
          plugins {
            id 'org.jetbrains.kotlin.jvm' version '{{ kotlin_version }}'
          }
          group 'org.example'
          version '1.0-SNAPSHOT'""".trimIndent()

    val BUILD_GRADLE_CONTENT = """
          plugins {
            id 'org.jetbrains.kotlin.jvm' version '1.3.71'
          }
          group 'org.example'
          version '1.0-SNAPSHOT'""".trimIndent()

    val VARIABLES = """
        kotlin_version: 1.3.71
    """.trimIndent()

    val TEMPLATE = """
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
              source: build.gradle
    """.trimIndent()
}