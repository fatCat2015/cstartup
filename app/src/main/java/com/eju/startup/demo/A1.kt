package com.eju.startup.demo

import android.content.Context
import android.util.Log
import com.eju.startup.Initializer
import kotlinx.coroutines.delay

class A1: Initializer<String> {

    override  fun create(context: Context):String {
        Thread.sleep(randomDelay)
        return "A1 result"
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }


}