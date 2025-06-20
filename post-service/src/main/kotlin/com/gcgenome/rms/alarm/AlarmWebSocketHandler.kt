package com.gcgenome.rms.alarm

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class AlarmWebSocketHandler(
    val notifier: AlarmNotifier,
    val objectMapper: ObjectMapper
) : WebSocketHandler{
    override fun handle(session: WebSocketSession): Mono<Void> {

        val uid = session.handshakeInfo.uri.query?.substringAfter("uid=", "") ?: return session.close()

        val outbound = notifier.streamFor(uid)
            .map { msg -> objectMapper.writeValueAsString(msg) }
            .map (session::textMessage)

        return session.send(outbound)
    }
}