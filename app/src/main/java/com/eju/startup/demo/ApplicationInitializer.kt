package com.eju.startup.demo

import android.content.Context
import android.util.Log
import com.eju.startup.Initializer
import kotlinx.coroutines.delay


class ApplicationInitializer: Initializer<String> {

    override  fun create(context: Context):String {
        Log.i(TAG, "${javaClass.simpleName} create start ${Thread.currentThread().id}")
        Thread.sleep(randomDelay)
        Log.i(TAG, "${javaClass.simpleName} create end ${Thread.currentThread().id}")
        return "A0 result"
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}