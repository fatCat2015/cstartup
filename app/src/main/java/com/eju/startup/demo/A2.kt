package com.eju.startup.demo

import android.content.Context
import android.util.Log
import com.eju.startup.Initializer
import kotlinx.coroutines.delay

class A2: Initializer<Unit> {

    override fun create(context: Context) {
        Thread.sleep(randomDelay)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}