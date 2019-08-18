package com.pos.services

import com.pos.domain.ConnectionEntry
import org.springframework.stereotype.Service
import java.net.Socket
import java.util.concurrent.ThreadLocalRandom

@Service
class ConnectionCheckingService(
) {


    fun checkConnection(config: ConnectionEntry):Boolean{


       return try {
           Thread.sleep(700)
//            val socket = Socket(config.ip, config.port).apply {
//                soTimeout = 3000
//            }.also {
//                println("DoneChecking")
//            }
//            socket.isConnected

           ThreadLocalRandom.current().nextBoolean()
       }catch (ex: Exception){
           println("NOPE")
           false
       }

    }


}