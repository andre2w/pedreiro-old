package com.github.andre2w.pedreiro

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object StartingSimpleProject : Spek({

    describe("The Pedreiro cli") {
        val baseDir = "/home/user/projects"
        val homeDir = "/home/user/pedreiro"
        val templateName = "baseGradle"

        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<Environment>(relaxUnitFun = true)
        val pedreiro = Pedreiro(fileSystemHandler, environment)

        describe("creating project from a simple template with only folders and files") {
            every { environment.currentDir() } returns baseDir
            every { environment.userHome() } returns homeDir
            every { fileSystemHandler.readFile("$homeDir/.pedreiro/configuration.yml") } returns Fixtures.CONFIGURATION
            every { fileSystemHandler.readFile("$homeDir/.pedreiro/templates/${templateName}.yml") } returns Fixtures.SIMPLE_TEMPLATE
            pedreiro.execute(arrayOf(templateName))

            it("should create the file structure declared in the template") {

                verify {
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/kotlin")
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/resources")
                    fileSystemHandler.createFile("${baseDir}/test/src/build.gradle", Fixtures.BUILD_GRADLE_CONTENT)
               }
            }
        }
    }
})

