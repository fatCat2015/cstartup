package com.eju.startup.demo

import android.content.Context
import android.util.Log
import com.eju.startup.Initializer
import kotlinx.coroutines.delay

class C1: Initializer<Unit> {

    override fun create(context: Context) {
        Log.i(TAG, "${javaClass.simpleName} create start ${Thread.currentThread().id}")
        Thread.sleep(randomDelay)
        Log.i(TAG, "${javaClass.simpleName} create end ${Thread.currentThread().id}")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(B0::class.java,B2::class.java)
    }
}