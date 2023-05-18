package com.gcgenome.rms.data

import reactor.core.publisher.Flux

data class Service_(
    val id: String,
    val name: String,
    var extensions: List<Extension_> = emptyList(),
    var sampleTypes: List<SampleType_> = emptyList()
) {
    companion object {
        data class ServiceExtensionBuilder (
            val service: String,
            val id: String,
            val name: String?,
            val required: Boolean?,
            val regex: String?,
        )
        data class ServiceSampleTypeBuilder (
            val service: String,
            val id: String,
            val name: String
        )
        fun Flux<ServiceExtensionBuilder>.groupByService() = extensionBuild(this)
        private fun extensionBuild(flux: Flux<ServiceExtensionBuilder>): Flux<Pair<String, List<Extension_>>> = flux
            .groupBy { it.service }
            .flatMap { pair -> pair.collectList().map { Pair(pair.key(), it) } }
            .map { (key, list) ->
                val extensions = list
                    .mapNotNull {
                        Extension_(
                            id = it.id,
                            name = it.name,
                            regex = it.regex,
                            required = it.required
                        )
                    }.distinct()
                Pair(key, extensions)
            }
        fun Flux<ServiceSampleTypeBuilder>.groupBySampleType() = sampleTypeBuild(this)
        private fun sampleTypeBuild(flux: Flux<ServiceSampleTypeBuilder>): Flux<Pair<String, List<SampleType_>>> = flux
            .groupBy{it.service}
            .flatMap { pair -> pair.collectList().map { Pair(pair.key(), it) } }
            .map { (key,list) ->
                val sampleTypes = list
                    .mapNotNull {
                        SampleType_(
                            id = it.id,
                            name = it.name
                        )
                    }.distinct()
                Pair(key, sampleTypes)
            }

        data class ServiceBuilder (
            val id: String,
            val name: String,
        ) {
            fun build(extensions: List<Extension_>, sampleTypes: List<SampleType_>): Service_ = Service_(
                id= this.id,
                name=this.name,
                extensions = extensions,
                sampleTypes = sampleTypes
            )
        }
    }
}