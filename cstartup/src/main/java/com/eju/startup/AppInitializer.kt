package com.eju.startup

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.startup.R
import com.eju.startup.AppInitializer.doInitialize
import kotlinx.coroutines.*
import java.lang.NullPointerException
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
object AppInitializer{

    private val context:Context = InitializationProvider.sContext

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val mInitializedValue: MutableMap<Class<*>, Any> = hashMapOf()
    /**
     * 保存通过反射创建的Initializer
     */
    private val createdInitializers :HashMap<String,Initializer<*>> = hashMapOf()

    fun <T> getInitializedValue(clazz: Class<*>) = (mInitializedValue[clazz] as T)?:throw  StartupException(NullPointerException("${clazz.simpleName} has not initialized"))

    fun cancel() = coroutineScope.cancel()

    internal fun discoverAndInitialize() {
        coroutineScope.launch {
            _discoverAndInitialize()
        }
    }

    private suspend fun CoroutineScope._discoverAndInitialize(){
        try {
            val provider = ComponentName(context.packageName, InitializationProvider::class.java.name)
            val providerInfo = context.packageManager.getProviderInfo(provider, PackageManager.GET_META_DATA)
            val metadata = providerInfo.metaData
            val startup = context.getString(R.string.eju_startup)
            if (metadata != null) {

                val keys = metadata.keySet()
                /**
                 * 保存所有定义的Initializer(包含在meta-data中未定义的Initializer),和Initializer依赖的initializers
                 */
                val initializers: HashMap<Initializer<*>, HashSet<Initializer<*>>> = hashMapOf()

                /**
                 * 保存已经放在initializers中的Initializer的className
                 */
                val addedInitializerClassNames :HashSet<String> = hashSetOf()

                /**
                 * 保存Initializer和其对应的所有的前节点
                 */
                val initializerAllDependencies :HashMap<Initializer<*>,HashSet<Initializer<*>>> = hashMapOf()

                for (key in keys) {
                    val value = metadata.getString(key, null)
                    if (startup == value) {
                        val clazz = Class.forName(key)
                        if (Initializer::class.java.isAssignableFrom(clazz)) {
                            val component = clazz as Class<out Initializer<*>>
                            val initializer :Initializer<*> = getOrCreateInitializerInstance(component)
                            assembleDependencies(initializer,initializers,addedInitializerClassNames)
                        }
                    }
                }
                assembleInitializerDependencies(initializers,initializerAllDependencies)
                doInitialize(initializerAllDependencies)
            }
        } catch (exception: Throwable) {
            throw StartupException(exception)
        }
    }


    private suspend fun CoroutineScope.doInitialize(initializerAllDependencies :HashMap<Initializer<*>,HashSet<Initializer<*>>>) {
        var toCreateInitializers = initializerAllDependencies.filterValues { it.isEmpty()}
        while (toCreateInitializers.isNotEmpty()){
            val awaitList: List<Deferred<*>> = toCreateInitializers.keys
                .onEach { initializer->
                    initializerAllDependencies.remove(initializer)
                    initializerAllDependencies.values.forEach {
                        it.remove(initializer)
                    }
                }.map {
                    async {
                        mInitializedValue[it.javaClass] = it.create(context) as Any
                    }
                }
            awaitList.forEach {
                it.await()
            }
            toCreateInitializers = initializerAllDependencies.filterValues { it.isEmpty()}
        }
    }


    fun initializeComponent(component: Class<out Initializer<*>?>){
        coroutineScope.launch {
            val initializers: HashMap<Initializer<*>, HashSet<Initializer<*>>> = hashMapOf()
            val addedInitializerClassNames :HashSet<String> = hashSetOf()
            val initializer :Initializer<*> = getOrCreateInitializerInstance(component)
            assembleDependencies(initializer,initializers,addedInitializerClassNames)
            val initializerAllDependencies :HashMap<Initializer<*>,HashSet<Initializer<*>>> = hashMapOf()
            assembleInitializerDependencies(initializers,initializerAllDependencies)
            doInitialize(initializerAllDependencies)
        }
    }

    private fun assembleDependencies(initializer :Initializer<*>,
                                     initializers: HashMap<Initializer<*>, HashSet<Initializer<*>>>,
                                     addedInitializerClassNames :HashSet<String>){
        val initializerClassName = initializer.javaClass.name
        if(!addedInitializerClassNames.contains(initializerClassName)){
            initializers[initializer] =  initializer.dependencies().map { getOrCreateInitializerInstance(it)  }.toHashSet().onEach {
                assembleDependencies(it,initializers,addedInitializerClassNames)
            }
            addedInitializerClassNames.add(initializerClassName)
        }
    }


    private fun getOrCreateInitializerInstance(component:Class<out Initializer<*>>):Initializer<*>{
        return createdInitializers[component.name]?:component.getDeclaredConstructor().newInstance().also {
            createdInitializers[component.name] = it
        }
    }


    private fun assembleInitializerDependencies(
        initializers: HashMap<Initializer<*>, HashSet<Initializer<*>>>,
        initializerAllDependencies: HashMap<Initializer<*>, HashSet<Initializer<*>>>
    ) {
        initializers.forEach{
            val initializer = it.key
            val initializerDependencies = it.value
            if(initializerDependencies.isEmpty()){
                initializerAllDependencies[initializer] = initializerDependencies
            }else{
                val dependencies = HashSet(initializerDependencies)
                initializerAllDependencies[initializer] = dependencies
                initializerDependencies.forEach {
                    getAllDependencies(it,initializers,dependencies)
                }
            }
        }
    }

    private fun getAllDependencies(initializer:Initializer<*>,
                                   initializers: HashMap<Initializer<*>,HashSet<Initializer<*>>>,
                                   dependencies:HashSet<Initializer<*>>){

        val initializerDependencies = initializers[initializer]
        if(initializerDependencies.isNullOrEmpty()){
            return
        }else{
            dependencies.addAll(HashSet(initializerDependencies))
            initializerDependencies.forEach {
                getAllDependencies(it,initializers,dependencies)
            }
        }
    }






}