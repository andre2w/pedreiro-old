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

object BlueprintWithVariables : Spek({

    describe("The Pedreiro cli") {
        val baseDir = "/home/user/projects"
        val homeDir = "/home/user/pedreiro"
        val blueprintName = "baseGradle"
        val configurationPath = "$homeDir/.pedreiro/configuration.yml"
        val blueprintPath = "$homeDir/.pedreiro/blueprints/${blueprintName}.yml"

        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<Environment>(relaxUnitFun = true)
        val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)
        val processExecutor = mockk<ProcessExecutor>(relaxUnitFun = true)

        val pedreiro = Pedreiro(fileSystemHandler, environment, consoleHandler, processExecutor)

        describe("createting a project form a blueprint with variables") {
            every { environment.currentDir() } returns baseDir
            every { environment.userHome() } returns homeDir
            every { fileSystemHandler.readFile(configurationPath) } returns Fixtures.CONFIGURATION
            every { fileSystemHandler.readFile(blueprintPath) } returns Fixtures.TEMPLATE_WITH_VARIABLES

            pedreiro.execute(arrayOf(blueprintName,"-a","project_name=new-project","--arg","package_name=com.test"))

            it("should create resources with values passed as parameter") {
                verify {
                    fileSystemHandler.createFolder("$baseDir/new-project")
                    fileSystemHandler.createFile("$baseDir/new-project/build.gradle", Fixtures.BUILD_GRADLE_WITH_VARIABLE)
                }
            }
        }
    }
})