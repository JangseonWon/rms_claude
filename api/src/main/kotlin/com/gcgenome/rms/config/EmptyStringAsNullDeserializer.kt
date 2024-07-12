package com.gcgenome.rms.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class EmptyStringAsNullDeserializer : JsonDeserializer<String>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): String? {
        val value = p.valueAsString
        return if (value.isEmpty()) null else value
    }
}