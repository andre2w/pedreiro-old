package com.github.andre2w.pedreiro

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object StartingSimpleProject : Spek({

    describe("The Pedreiro cli") {
        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<Environment>()
        val scaffoldingService = ScaffoldingService(fileSystemHandler, environment)
        val configuration = PedreiroConfiguration("/home/user/.pedreiro")
        val templateService = TemplateService(configuration, fileSystemHandler)
        val argumentParser = ArgumentParser()

        val baseDir = "/home/user/projects"
        val templateName = "baseGradle"

        every { environment.currentDir() } returns baseDir
        every { fileSystemHandler.readFile("/home/user/.pedreiro/${templateName}.yml") } returns Fixtures.simpleTemplate

        val pedreiro = Pedreiro(scaffoldingService, templateService, argumentParser)

        describe("when called with a template name as an argument") {
            pedreiro.execute(arrayOf(templateName))

            it("should create the file structure declared in the template") {
                val fileContent = """
                plugins {
                    id 'org.jetbrains.kotlin.jvm' version '1.3.71'
	            }
	
	            group 'org.example'
	            version '1.0-SNAPSHOT'""".trimIndent()

                verify {
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/kotlin")
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/resources")
                    fileSystemHandler.createFile("${baseDir}/test/build.gradle", fileContent)
               }
            }
        }
    }
})