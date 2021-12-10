package com.eju.startup.demo

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.eju.startup.AppInitializer
import com.eju.startup.Initializer
import kotlinx.coroutines.*
import kotlin.coroutines.resume

class PrivacyPolicyInitializer:Initializer<Unit> {

    override  fun create(context: Context) {
        Log.i(TAG, "${javaClass.simpleName} create start ${Thread.currentThread().id}")
        val result = when{
            hasAlreadyAgreedPrivacyPolicy() ->{
               true
            }
            else -> {
            }
        }

        Log.i(TAG, "${javaClass.simpleName} create end ${Thread.currentThread().id} ${result}")

    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}

private suspend fun showPrivacyPolicyDialog(context: Context):Boolean{
    return suspendCancellableCoroutine { continuation->
        AlertDialog.Builder(context)
            .setTitle("隐私政策说明")
            .setMessage("隐私政策说明隐私政策说明隐私政策说明隐私政策说明隐私政策说明隐私政策说明隐私政策说明隐私政策说明")
            .setPositiveButton("知道了",object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    agreedPrivacyPolicy(true)
                    dialog?.dismiss()
                }
            })
            .setNegativeButton("大麦",object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    agreedPrivacyPolicy(false)
                    dialog?.dismiss()
                }
            })
            .setOnDismissListener {
                continuation.resume(hasAlreadyAgreedPrivacyPolicy())
            }
            .show()
    }
}