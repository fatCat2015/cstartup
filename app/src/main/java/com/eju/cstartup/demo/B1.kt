package com.eju.cstartup.demo

import android.content.Context
import android.util.Log
import com.eju.cstartup.Initializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class B1: Initializer {

    override suspend fun create(context: Context) {
        withContext(Dispatchers.Main){
            Log.i(TAG, "${B1::class.java.simpleName} create start ${Thread.currentThread().id}")
            delay(randomDelay)
            Log.i(TAG, "${B1::class.java.simpleName} create end ${Thread.currentThread().id}")
        }
    }

    override fun dependencies(): List<Class<out Initializer>> {
        return listOf(A2::class.java,A3::class.java)
    }
}