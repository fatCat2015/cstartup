package com.eju.cstartup

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.startup.R
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.HashMap
import java.util.HashSet

/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ /**
 * An [AppInitializer] can be used to initialize all discovered [Initializer]s.
 * <br></br>
 * The discovery mechanism is via `<meta-data>` entries in the merged `AndroidManifest.xml`.
</meta-data> */
@SuppressLint("StaticFieldLeak")
object AppInitializer {

    private val context:Context = InitializationProvider.sContext

    private val mInitializedValues: MutableMap<Initializer<*>, Any> = HashMap()

    /**
     * key:   Initializer
     * value: Initializer 依赖的Initializers
     */
    private val initializers: HashMap<Initializer<*>, HashSet<Initializer<*>>> = HashMap()

    /**
     * Initializer前驱节点数量
     */
    private val initializerPreCount :HashMap<Initializer<*>,Int> = HashMap()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

//    private suspend fun <T> doInitialize(
//        component: Class<out Initializer<*>>,
//    ): T {
//        return try {
//            val result: Any?
//            if (!mInitialized.containsKey(component)) {
//                try {
//                    val instance: Any? = component.getDeclaredConstructor().newInstance()
//                    val initializer = instance as Initializer<*>?
//                    val dependencies = initializer!!.dependencies()
//                    if (!dependencies.isEmpty()) {
//                        for (clazz in dependencies) {
//                            if (!mInitialized.containsKey(clazz)) {
//                                doInitialize<Any>(clazz!!)
//                            }
//                        }
//                    }
//                    result = initializer.create(mContext)
//                    mInitialized[component] = result
//                } catch (throwable: Throwable) {
//                    throw StartupException(throwable)
//                }
//            } else {
//                result = mInitialized[component]
//            }
//            result as T
//        } finally {
//        }
//
//    }
//
//    internal fun discoverAndInitialize() {
//        try {
//            val provider = ComponentName(
//                mContext.packageName,
//                InitializationProvider::class.java.name
//            )
//            val providerInfo = mContext.packageManager.getProviderInfo(provider, PackageManager.GET_META_DATA)
//            val metadata = providerInfo.metaData
//            val startup = mContext.getString(R.string.androidx_cstartup)
//            if (metadata != null) {
//                val keys = metadata.keySet()
//                for (key in keys) {
//                    val value = metadata.getString(key, null)
//                    if (startup == value) {
//                        val clazz = Class.forName(key)
//                        if (Initializer::class.java.isAssignableFrom(clazz)) {
//                            val component = clazz as Class<out Initializer<*>>
//                            coroutineScope.launch {
//                                doInitialize<Any>(component)
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (exception: PackageManager.NameNotFoundException) {
//            throw StartupException(exception)
//        } catch (exception: ClassNotFoundException) {
//            throw StartupException(exception)
//        } finally {
//        }
//    }


    internal fun discoverAndInitialize() {
        coroutineScope.launch {
            assembleInitializes()
            assemblePreCount()
            delay(3000)
            doInitialize()
            Log.i("sck220", "discoverAndInitialize: end")
        }
    }

    private suspend fun CoroutineScope.doInitialize() {
        val awaitList: List<Deferred<*>> = initializerPreCount.filterValues { it == 0 }.keys
            .onEach { initializer->
                initializerPreCount.remove(initializer)
                aa(initializer)
            }.map {
                async (Dispatchers.IO){
                    it.create(context)
                }
            }
        awaitList.forEach {
            it.await()
        }
    }

    private fun aa(initializer :Initializer<*>){
        initializers.forEach {
            if(it.value.contains(initializer)){

            }
        }
    }



    private fun assembleInitializes(){
        try {
            val provider = ComponentName(context.packageName, InitializationProvider::class.java.name)
            val providerInfo = context.packageManager.getProviderInfo(provider, PackageManager.GET_META_DATA)
            val metadata = providerInfo.metaData
            val startup = context.getString(R.string.androidx_cstartup)
            if (metadata != null) {
                val keys = metadata.keySet()
                for (key in keys) {
                    val value = metadata.getString(key, null)
                    if (startup == value) {
                        val clazz = Class.forName(key)
                        if (Initializer::class.java.isAssignableFrom(clazz)) {
                            val component = clazz as Class<out Initializer<*>>
                            val initializer :Initializer<*> = newInitializerInstance(component)
                            assembleDependencies(initializer)
                        }
                    }
                }
            }
        } catch (exception: Throwable) {
            throw StartupException(exception)
        }
    }

    private fun assembleDependencies(initializer :Initializer<*>){
        if(!initializers.keys.map { it.javaClass.name }.contains(initializer.javaClass.name)){
            initializers[initializer] =  initializer.dependencies().map { newInitializerInstance(it)  }.toHashSet().also {
                it.forEach {
                    assembleDependencies(it)
                }
            }
        }
    }


    private fun assemblePreCount(){
        initializers.keys.forEach { initializer->
            initializerPreCount[initializer] = getInitializePreCount(initializer)
        }
        initializers.forEach {
            Log.i("sck220", "initialize: ${it.key.javaClass.simpleName} = ${it.value.map { it.javaClass.simpleName }.joinToString { it }}")
        }
        initializerPreCount.forEach {
            Log.i("sck220", "initializePreCount: ${it.key.javaClass.simpleName} = ${it.value}")
        }

    }

    private fun getInitializePreCount(initializer :Initializer<*>):Int{
        val dependencies = initializers[initializer]
        if(dependencies.isNullOrEmpty()){
            return 0
        }else{
            return dependencies.fold(dependencies.size){value,item->
                value + getInitializePreCount(item)
            }
        }
    }

    private fun newInitializerInstance(component:Class<out Initializer<*>>):Initializer<*>{
        return component.getDeclaredConstructor().newInstance()
    }







}