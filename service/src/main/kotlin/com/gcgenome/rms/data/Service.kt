package com.gcgenome.rms.data

import com.gcgenome.rms.tables.references.SERVICE
import org.jooq.JSON
import org.jooq.Record4

data class Service(
    val id: String,
    val name: String,
    val sampleTypes: List<SampleType>,
    val extensions: List<Extension>?

){
    companion object{
        fun toModel(record: Record4<String?, String?, JSON?, JSON?>): Service{
            return Service(
                id = record.get(SERVICE.ID)!!,
                name = record.get(SERVICE.NAME)!!,
                sampleTypes = record.getValue("sampleTypes", Array<SampleType>::class.java).toList(),
                extensions = record.getValue("extensions", Array<Extension>::class.java)?.toList()
            )
        }
    }
}