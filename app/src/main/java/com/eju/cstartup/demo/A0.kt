package com.eju.cstartup.demo

import android.content.Context
import android.util.Log
import com.eju.cstartup.Initializer
import kotlinx.coroutines.delay

class A0: Initializer<Unit> {

    override suspend fun create(context: Context) {
        Log.i(TAG, "${javaClass.simpleName} create start ${Thread.currentThread().id}")
        delay(2000)
        Log.i(TAG, "${javaClass.simpleName} create end ${Thread.currentThread().id}")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(A1::class.java)
    }
}