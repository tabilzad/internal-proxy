package com.pos.config

import com.pos.domain.EntryCreationDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter
import org.springframework.integration.ip.udp.UnicastSendingMessageHandler
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap


@Configuration
class ProxyConfiguration {

    @Bean("cache")
    fun cache() = ConcurrentHashMap<String, EntryCreationDto>().apply {
        put("service1", EntryCreationDto("service1", "http://google.com", "<RESPONSE>1", true))
        put("service2", EntryCreationDto("service2", "http://google.com", "<RESPONSE>2", false))
        put("service3", EntryCreationDto("service3", "http://google.com", "<RESPONSE>3", true, status = HttpStatus.GATEWAY_TIMEOUT))
        put("service4", EntryCreationDto("service4", "http://google.com", "<RESPONSE>4", false))
    }


    @Bean
    fun restTemplate() = RestTemplate()

    @Bean
    fun processUniCastUdpMessage(@Value("\${udpServer.port}") port: Int): IntegrationFlow {
        return IntegrationFlows
            .from(UnicastReceivingChannelAdapter(port))
            .handle("UDPServer", "handleMessage")            .get()
    }

}

@Service
class UDPServer {
    //Receive
    fun handleMessage(message: Message<Any>) {
        val data = String(message.payload as ByteArray)
        print(data)
    }

    //Send
    fun send(host:String, port: Int, payload: String) {
        UnicastSendingMessageHandler(host, port).handleMessage(MessageBuilder.withPayload(payload).build())
    }
}
