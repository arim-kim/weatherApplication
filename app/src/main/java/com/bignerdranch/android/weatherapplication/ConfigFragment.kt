package com.bignerdranch.android.weatherapplication

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class ConfigFragment : Fragment() {

    lateinit var ip_adress: EditText
    lateinit var port_number: EditText
    private lateinit var setting_button: Button
    private lateinit var TEST_textbox: TextView
    private lateinit var send_meassage: EditText
    private lateinit var send_Button: Button
    private lateinit var recieved_message: TextView
    private lateinit var client : SocketClient

    private var isRunning=true
    private var iWantSend=false
    lateinit var txData : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //기본값


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 레이아웃과 연결해주기
        val view = inflater.inflate(R.layout.fragment_config, container, false)

        // 위젯 연결하기
        ip_adress = view.findViewById(R.id.ip_address) as EditText
        port_number = view.findViewById(R.id.port_number) as EditText
        setting_button = view.findViewById(R.id.setting_button) as Button
        TEST_textbox = view.findViewById(R.id.TEST_text) as TextView
        send_Button = view.findViewById(R.id.send_button) as Button
        send_meassage = view.findViewById(R.id.sending_message) as EditText
        recieved_message = view.findViewById(R.id.recieved_message) as TextView



        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }
    override fun onStart() {
        super.onStart()


        // ip, port
        setting_button.setOnClickListener {

            ClientThread().start()

        }


        send_Button.setOnClickListener {

            txData = send_meassage.text.toString()
            iWantSend = true


        }


    }


    companion object {
        fun newInstance(): ConfigFragment {
            return ConfigFragment()
        }

    }

    inner class ClientThread : Thread()
    {
        //클라이언트 소켓을 만들기 위해서 MainActivity 에 지연된 초기화를 하면서 socketClient 라는 속성을 정의 (SocketClient 타입)
        //private lateinit var socketClient: SocketClient
        private lateinit var rxString : String

        override fun run()
        {
            // 클래스 인스턴스 선언후 바로 사용
            client = SocketClient()
            client.connect(ip_adress.text.toString(), port_number.toString().toInt())


            while(isRunning){

                //수신 처리 (수신된 데이터가 있으면)
                var available = client.availableCount()  //제네릭
                if (available > 0){
                    rxString = client.read(available)
                    //println("data : ${data}")
                    Log.d("수신시간",System.currentTimeMillis().toString())
                    Log.d("수신 데이터 ",rxString)

                    activity?.runOnUiThread{  //inner(내부) 클레스에서 UI 스레드 접근 할때 필요
                        recieved_message.text=rxString
                    }
                }

                //송신 처리(UI에서 송신 요구 했다면 )
                if(iWantSend == true){
                    Log.d("송신시간",System.currentTimeMillis().toString())
                    Log.d("송신 데이터 ","txString")

                    //client.sendData("You are a good friend")
                    client.sendData(txData)
                    iWantSend = false
                }

                SystemClock.sleep(100) //cpu sleep 0.1초
            }
            client.closeConnect()

        }
    }
}

