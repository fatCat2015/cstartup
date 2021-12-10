package com.eju.startup.demo

import android.content.Context
import android.util.Log
import com.eju.startup.AppInitializer
import com.eju.startup.Initializer
import kotlinx.coroutines.delay
import java.lang.Exception
import java.lang.NullPointerException

open class A0: Initializer<Unit> {

    override fun create(context: Context) {
        Thread.sleep(randomDelay)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(A1::class.java)
    }


}