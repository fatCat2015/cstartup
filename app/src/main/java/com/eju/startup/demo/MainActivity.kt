package com.eju.startup.demo

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.postDelayed
import com.eju.startup.AppInitializer
import com.eju.startup.Initializer
import com.eju.startup.R
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

const val TAG = "sck220"
val randomDelay :Long get() = Random().nextInt(1)*1000L+1000

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if(!hasAlreadyAgreedPrivacyPolicy()){
            AlertDialog.Builder(this)
                .setTitle("隐私政策说明")
                .setMessage("隐私政策说明隐私政策说明隐私政策说明隐私政策说明隐私政策说明隐私政策说明隐私政策说明隐私政策说明")
                .setPositiveButton("知道了"
                ) { dialog, which ->
                    agreedPrivacyPolicy(true)
                    dialog?.dismiss()
                }
                .setNegativeButton("不同意") { dialog, which ->
                    agreedPrivacyPolicy(false)
                    dialog?.dismiss()
                }
                .setOnDismissListener {
                    if(hasAlreadyAgreedPrivacyPolicy()){
                        AppInitializer.initializeComponent(C0::class.java,C1::class.java){
                            Log.i(TAG, "onCreate: 111111")
                        }
                    }
                }
                .show()
        }else{
            AppInitializer.initializeComponent(C0::class.java,C1::class.java){
                Log.i(TAG, "onCreate: 111111")
            }
        }


        val tv = findViewById<TextView>(R.id.tvTest)
        val tv1 = findViewById<TextView>(R.id.tvTest1)

        tv.setOnClickListener{
            AppInitializer.initializeComponent(C0::class.java)
        }

        tv1.setOnClickListener{
//            AppInitializer.initializeComponent(C1::class.java)
        }



    }
}