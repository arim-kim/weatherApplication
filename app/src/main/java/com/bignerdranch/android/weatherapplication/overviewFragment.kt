package com.bignerdranch.android.weatherapplication

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_FORMAT = "yyyy년 M월 d일 H시 m분 : E요일"
private const val TAG = "LED 상태 확인"


class overviewFragment  : Fragment() {

    private lateinit var sensorTextView: TextView
    private lateinit var sensorSetting : TextView
    private lateinit var dateView : TextView
    private lateinit var finedust2_5_levelTextView : TextView
    private lateinit var finedust10_levelTextView : TextView
    private lateinit var finedust2_5_level : TextView
    private lateinit var finedust10_level : TextView
    private lateinit var LED_TextView: TextView
    private lateinit var LEDSeekBar : SeekBar
    private lateinit var GoConfig : Button


    private lateinit var client : SocketClient
    private var isRunning=true
    private var iWantSend=false
    private lateinit var LED_data : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃과 연결해주기
        val view = inflater.inflate(R.layout.fragment_overview, container, false)

        // 위젯 연결
        sensorTextView = view.findViewById(R.id.sensor_setting_text) as TextView
        sensorSetting = view.findViewById(R.id.sensor_setting) as TextView
        dateView = view.findViewById(R.id.date) as TextView
        finedust2_5_levelTextView = view.findViewById(R.id.pm2_5_text) as TextView
        finedust2_5_level = view.findViewById(R.id.pm2_5_level) as TextView
        finedust10_levelTextView = view.findViewById(R.id.pm10_text) as TextView
        finedust10_level = view.findViewById(R.id.pm10_level) as TextView
        LED_TextView = view.findViewById(R.id.LED_text) as TextView
        LEDSeekBar = view.findViewById(R.id.LED_Setter) as SeekBar
        GoConfig = view.findViewById(R.id.Go_configButton) as Button




        val now = System.currentTimeMillis()
        val date = Date(now)
        val dateFormat = SimpleDateFormat(DATE_FORMAT)
        dateView.text = dateFormat.format(date)


        GoConfig.setOnClickListener {
            callbacks?.GoConfig()
        }


        return view
    }

    override fun onStart() {
        super.onStart()
        ClientThread().start()

        LEDSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d(TAG, "LED : " + progress.toString())
                LED_data = progress.toString()
                iWantSend = true
                // 숫자값을 쓰려면 그냥 progress

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    companion object {
        fun newInstance() : overviewFragment {
            return overviewFragment()
        }
    }

    //    호스팅 액티비티에서 구현할 인터페이스
    interface Callbacks {
        fun GoConfig()
    }

    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    inner class ClientThread : Thread()
    {
        //클라이언트 소켓을 만들기 위해서 MainActivity 에 지연된 초기화를 하면서 socketClient 라는 속성을 정의 (SocketClient 타입)
        //private lateinit var socketClient: SocketClient

        override fun run()
        {


            //클래스 인스턴스 선언후 바로 사용
            client = SocketClient()
//            client.connect(ConfigFragment().ip_adress.text.toString(), ConfigFragment().port_number.toString().toInt())
            client.connect("172.30.1.11",54321)


            while(isRunning){

                // 미세먼지 데이터 받기
                //수신 처리 (수신된 데이터가 있으면)
//                var available = client.availableCount()  //제네릭
//                if (available > 0){
//                    rxString = client.read(available)
//                    //println("data : ${data}")
//                    Log.d("수신시간",System.currentTimeMillis().toString())
//                    Log.d("수신 데이터 ",rxString)
//
//                    activity?.runOnUiThread{  //inner(내부) 클레스에서 UI 스레드 접근 할때 필요
//                        recieved_message.text=rxString
//                    }
//                }


                //송신 처리(UI에서 송신 요구 했다면 )
                if(iWantSend == true){
                    Log.d("송신시간",System.currentTimeMillis().toString())
                    Log.d("송신 데이터 ","LED_data")

                    //client.sendData("You are a good friend")
                    client.sendData(LED_data)
                    iWantSend = false
                }

                SystemClock.sleep(100) //cpu sleep 0.1초

            }
            client.closeConnect()

        }
    }

}