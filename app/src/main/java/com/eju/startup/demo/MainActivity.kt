package com.eju.startup.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.postDelayed
import com.eju.startup.AppInitializer
import com.eju.startup.R
import java.util.*

const val TAG = "sck220"
val randomDelay :Long get() = Random().nextInt(5)*1000L+1000

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv = findViewById<TextView>(R.id.tvTest)

        tv.setOnClickListener{
            AppInitializer.initializeComponent(A2::class.java)
            Toast.makeText(this, "11111", Toast.LENGTH_SHORT).show()
        }

        tv.postDelayed(2000){
//            startActivity(Intent(this,MainActivity2::class.java))
        }


    }
}