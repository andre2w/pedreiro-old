package com.github.andre2w.pedreiro

import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object StartingSimpleProject : Spek({

    describe("The Pedreiro cli") {
        val fileSystemHandler = mockk<FileSystemHandler>()
        val scaffoldingService = ScaffoldingService(fileSystemHandler)
        val configuration = PedreiroConfiguration("/home/user/.pedreiro")
        val templateService = TemplateService(configuration, fileSystemHandler)
        val argumentParser = ArgumentParser()
        val pedreiro = Pedreiro(scaffoldingService, templateService, argumentParser)

        describe("when called with a template name as an argument") {
            val templateName = "baseGradle"
            pedreiro.execute(arrayOf(templateName))

            it("should create the file structure declared in the template") {
                val fileContent = """
                plugins {
                    id 'org.jetbrains.kotlin.jvm' version '1.3.71'
	            }
	
	            group 'org.example'
	            version '1.0-SNAPSHOT'""".trimIndent()

                verify {
                    fileSystemHandler.createFolder("test/src/main/kotlin")
                    fileSystemHandler.createFolder("test/src/main/resources")
                    fileSystemHandler.createFolder("test/src/test/kotlin")
                    fileSystemHandler.createFolder("test/src/test/resources")
                    fileSystemHandler.createFile("test/build.gradle", fileContent)
               }
            }
        }
    }
})