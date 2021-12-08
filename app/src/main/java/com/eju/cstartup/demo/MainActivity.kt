package com.eju.cstartup.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.eju.cstartup.R
import java.util.*

const val TAG = "sck220"
val randomDelay :Long get() = Random().nextInt(5)*1000L+1000

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<TextView>(R.id.tvTest).setOnClickListener{
            Toast.makeText(this, "11111", Toast.LENGTH_SHORT).show()
        }
    }
}