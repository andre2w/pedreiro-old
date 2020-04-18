package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.io.ConsoleHandler
import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.ProcessExecutor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object BlueprintFromFolder : Spek({

    describe("The Pedriero cli") {
        val baseDir = "/home/user/projects"
        val homeDir = "/home/user/pedreiro"
        val blueprintName = "folder-blueprint"
        val configurationPath = "$homeDir/.pedreiro/configuration.yml"
        val blueprintPath = "$homeDir/.pedreiro/blueprints/${blueprintName}"

        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<Environment>(relaxUnitFun = true)
        val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)
        val processExecutor = mockk<ProcessExecutor>(relaxUnitFun = true)

        val pedreiro = Pedreiro(fileSystemHandler, environment, consoleHandler, processExecutor)

        describe("when creating a project from a template in a folder") {
            every { environment.currentDir() } returns baseDir
            every { environment.userHome() } returns homeDir
            every { fileSystemHandler.readFile(configurationPath) } returns SimpleFixtures.CONFIGURATION
            every { fileSystemHandler.isFolder(blueprintPath) } returns true
            every { fileSystemHandler.readFile("$blueprintPath/blueprint.yml") } returns FolderFixtures.TEMPLATE
            every { fileSystemHandler.readFile("$blueprintPath/variables.yml") } returns FolderFixtures.VARIABLES
            every { fileSystemHandler.readFile("$blueprintPath/build.gradle") } returns FolderFixtures.BUILD_GRADLE_TEMPLATE

            pedreiro.execute(arrayOf(blueprintName))

            it("should create files and folders with variables replaced") {
                verify {
                    fileSystemHandler.createFolder("$baseDir/test")
                    fileSystemHandler.createFolder("$baseDir/test/src")
                    fileSystemHandler.createFolder("$baseDir/test/src/main")
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/kotlin")
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/resources")
                    fileSystemHandler.createFile("${baseDir}/test/build.gradle", FolderFixtures.BUILD_GRADLE_CONTENT)
                }
            }
        }
    }

})