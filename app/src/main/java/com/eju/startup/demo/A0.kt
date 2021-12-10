package com.eju.startup.demo

import android.content.Context
import android.util.Log
import com.eju.startup.AppInitializer
import com.eju.startup.Initializer
import kotlinx.coroutines.delay
import java.lang.Exception
import java.lang.NullPointerException

class A0: Initializer<Unit> {

    override fun create(context: Context) {
        Log.i(TAG, "${javaClass.simpleName} create start ${Thread.currentThread().id}")
        Thread.sleep(randomDelay)
        Log.i(TAG, "${javaClass.simpleName} a1 result ${Thread.currentThread().id}  ${AppInitializer.getInitializedValue<String>(A1::class.java)}")
        Log.i(TAG, "${javaClass.simpleName} create end ${Thread.currentThread().id}")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(A1::class.java)
    }


}