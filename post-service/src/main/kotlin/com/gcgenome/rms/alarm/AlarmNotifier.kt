package com.gcgenome.rms.alarm

import com.gcgenome.rms.data.AlarmMessage
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Component
class AlarmNotifier {
    private val sink = Sinks.many().multicast().directBestEffort<AlarmMessage>()

    fun publish(msg: AlarmMessage) = sink.tryEmitNext(msg)

    fun streamFor(userId: String): Flux<AlarmMessage> =
        sink.asFlux().filter { it.userId == userId }
}