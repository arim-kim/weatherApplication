package com.bignerdranch.android.weatherapplication

import java.io.InputStream
import java.io.OutputStream
import java.io.Serializable
import java.net.Socket

class SocketClient: Serializable {
    private lateinit var socket: Socket
    private lateinit var inputStream: InputStream
    lateinit var outputStream: OutputStream

    fun connect(ip: String, port: Int) {
        try {
            val socketAddress = ip    //"192.168.123.106" //InetAddress.getLocalHost()
            //println("socketAddress: $socketAddress")
            socket = Socket(socketAddress, port)
            outputStream = socket.getOutputStream()
            inputStream = socket.getInputStream()
        } catch (e: Exception) {
            //println("socket connect exception start!!")
            //println("e: $e")
        }
    }

    fun sendData(data: String) {
        //outputStream.write(
        //    (data + "\n").toByteArray(Charsets.UTF_8)
        //)

        //outputStream.write("Hello from the client!".toByteArray())
        outputStream.write(data.toByteArray())
    }

    fun flush() {
        outputStream.flush()
    }


    fun availableCount(): Int {
        return inputStream.available()
    }

    fun read(readCount: Int): String {
        val dataArr = ByteArray(readCount) // 사이즈에 맞게 byte array를 생성
        inputStream.read(dataArr) // byte array에 데이터를 씁니다.
        return String(dataArr) // byte array의 데이터를 통해 String을 생성
    }

    fun closeConnect() {
        outputStream.close()
        inputStream.close()
        socket.close()
    }
}