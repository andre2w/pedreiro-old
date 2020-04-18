package com.github.andre2w.pedreiro


object SimpleFixtures {


    val COMMAND_TEMPLATE = """
            ---
            - type: folder
              name: test
              children: 
                - type: command
                  command: gradle init --type java-application --test-framework junit --dsl groovy --project-name test --package com.example.test
        """.trimIndent()

    val CONFIGURATION = """
            ---
            blueprintsFolder: "/home/user/pedreiro/.pedreiro/blueprints"
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

    val BUILD_GRADLE_WITH_VARIABLE = """
        plugins {
          id 'org.jetbrains.kotlin.jvm' version '1.3.71'
        }
        group 'com.test'
        version '1.0-SNAPSHOT'""".trimIndent()

    val TEMPLATE_WITH_VARIABLES = """
        ---
        - type: folder
          name: "{{ project_name }}"
          children:
            - type: file
              name: build.gradle
              content: |-
                plugins {
                  id 'org.jetbrains.kotlin.jvm' version '1.3.71'
                }
                group '{{ package_name }}'
                version '1.0-SNAPSHOT'""".trimIndent()
}