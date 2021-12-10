package com.eju.startup.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.postDelayed
import com.eju.startup.AppInitializer
import com.eju.startup.Initializer
import com.eju.startup.R
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

const val TAG = "sck220"
val randomDelay :Long get() = Random().nextInt(1)*1000L+1000

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val tv = findViewById<TextView>(R.id.tvTest)
        val tv1 = findViewById<TextView>(R.id.tvTest1)

        val demo = DemoInterfaceImpl()
        val reflections = Reflections("com.eju.")
        reflections.getSubTypesOf(DemoInterface::class.java).also {
            Log.i("sck220", "MainActivity:${it} ")
        }.forEach {
            Log.i("sck220", "MainActivity:${it} ")
        }

        tv.setOnClickListener{
            demo.test()
            AppInitializer.initializeComponent(C0::class.java)
        }

        tv1.setOnClickListener{
            AppInitializer.initializeComponent(C1::class.java)
        }



    }
}