package com.eju.cstartup.demo

import android.content.Context
import android.util.Log
import com.eju.cstartup.Initializer
import kotlinx.coroutines.delay

class C1: Initializer {

    override suspend fun create(context: Context) {
        Log.i(TAG, "${javaClass.simpleName} create start ${Thread.currentThread().id}")
        delay(randomDelay)
        Log.i(TAG, "${javaClass.simpleName} create end ${Thread.currentThread().id}")
    }

    override fun dependencies(): List<Class<out Initializer>> {
        return listOf(B0::class.java,B2::class.java)
    }
}