package com.github.andre2w.pedreiro

import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object StartingSimpleProject : Spek({

    describe("The Pedreiro cli should use a template to") {

        describe("create a folder structure") {

            it("should have folders in right order") {
                Assertions.assertThat(true).isFalse()
            }
        }

    }

})