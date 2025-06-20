package com.gcgenome.rms.config

import com.gcgenome.rms.alarm.AlarmWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class WebSocketConfig {
    @Bean
    fun alarmMapping(alarmHandler: AlarmWebSocketHandler) =
        SimpleUrlHandlerMapping(
            mapOf("/w-api/post-service/ws/alarm" to alarmHandler),
            10
        )
    @Bean
    fun handlerAdapter() = WebSocketHandlerAdapter()
}