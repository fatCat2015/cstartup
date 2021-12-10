package com.eju.startup.demo

import android.content.Context
import android.util.Log
import com.eju.startup.Initializer
import kotlinx.coroutines.delay

class B0: Initializer<Unit> {

    override  fun create(context: Context) {
        Thread.sleep(randomDelay)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(A0::class.java)
    }
}