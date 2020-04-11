package com.github.andre2w.pedreiro.io

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.github.andre2w.pedreiro.blueprints.Blueprint
import com.github.andre2w.pedreiro.blueprints.CreateFile
import com.github.andre2w.pedreiro.blueprints.CreateFolder
import com.github.andre2w.pedreiro.blueprints.Task

class BlueprintDeserializer(vc: Class<*>?) : StdDeserializer<Blueprint>(vc) {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Blueprint {
        val levels = ArrayList<String>()
        val tasks = ArrayList<Task>()
        levels.add("")
        if (p != null && ctxt != null) {
            var value = p.nextValue()
            while (value != null || p.currentName == "NOT_AVAILABLE") {
                if (value.name == "START_OBJECT") {
                    value = p.nextValue()
                    val objectValues = HashMap<String, String>()

                    while (value.name == "VALUE_STRING") {
                        objectValues[p.currentName] = p.valueAsString
                        value = p.nextValue()
                    }

                    val task = if (objectValues["type"] == "file") {
                        CreateFile(objectValues["name"]!!, objectValues["content"]!!)
                    } else {
                        CreateFolder(objectValues["name"]!!)
                    }

                    tasks.add(task)
                }
                value = p.nextValue()

            }

        }

        return Blueprint(tasks)
    }

}