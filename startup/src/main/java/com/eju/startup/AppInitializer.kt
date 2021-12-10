package com.eju.startup

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import java.lang.NullPointerException
import java.util.HashMap
import java.util.HashSet
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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

    private val mInitializedValue: MutableMap<Class<*>, Any>  by lazy { hashMapOf() }

    private val createdInitializers :HashMap<String,Initializer<*>> by lazy { hashMapOf() }

    private val executorService :ExecutorService by lazy { Executors.newFixedThreadPool(4) }

    /**
     * 所有的Initializer和它的所有前节点
     */
    private val initializerAllDependencies :HashMap<Initializer<*>,HashSet<Initializer<*>>> by lazy { hashMapOf() }

    fun <T> getInitializedValue(clazz: Class<*>) = (mInitializedValue[clazz] as T)?:throw  StartupException(NullPointerException("${clazz.simpleName} has not initialized"))


    internal fun discoverAndInitialize() {
        assembleInitializerDependencies()
        initializeAutoCreatedInitializers()
    }


    private fun initializeAutoCreatedInitializers() {
        val initializers: HashMap<Initializer<*>, HashSet<Initializer<*>>> = hashMapOf()
        initializerAllDependencies.forEach {
            val initializer = it.key
            val initializerDependencies = it.value
            if (initializer.autoCreated() && initializerDependencies.all { it.autoCreated() }) {
                initializers[initializer] = initializerDependencies
            }
        }
        doInitialize(initializers)
    }


    fun initializeComponent(vararg components: Class<out Initializer<*>?>,completeCallback:()->Unit = {}){
        val initializers: HashMap<Initializer<*>, HashSet<Initializer<*>>> = hashMapOf()
        components.forEach { component->
            val targetInitializer = getOrCreateInitializerInstance(component)
            if(mInitializedValue[component] ==null){
                initializers[targetInitializer] = (initializerAllDependencies[targetInitializer]?: hashSetOf()).onEach {
                    initializers[it] = initializerAllDependencies[it]?: hashSetOf()
                }
            }
        }
        doInitialize(initializers)
        completeCallback.invoke()
    }


    private fun doInitialize(initializers :HashMap<Initializer<*>,HashSet<Initializer<*>>>) {
        var toCreateInitializers = initializers.filterValues { it.isEmpty()}.keys
        while (toCreateInitializers.isNotEmpty()){
            toCreateInitializers.filter { it.createOnMainThread() }.forEach { initializer->
                create(initializer)
            }
            toCreateInitializers.filter { !it.createOnMainThread() }.map { initializer->
                executorService.submit {
                    create(initializer)
                }
            }.map { it.get() }  //调用get 阻塞主线程 等待初始化完成
            reduce(toCreateInitializers,initializers)
            toCreateInitializers = initializers.filterValues { it.isEmpty()}.keys
        }
    }

    private fun create(initializer: Initializer<*>) {
        try {
            log("${initializer.javaClass.simpleName} start thread:${Thread.currentThread().id}")
            mInitializedValue[initializer.javaClass] = (initializer.create(context) as Any).also {
                log("${initializer.javaClass.simpleName} end result:${it}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            initializer.onFailed(e)
            log("${initializer.javaClass.simpleName} create failed ${e.message}")
        }
    }

    private fun reduce(
        createdInitializers :Set<Initializer<*>>,
        initializers: HashMap<Initializer<*>, HashSet<Initializer<*>>>
    ) {
        createdInitializers.forEach { initializer->
            initializers.remove(initializer)
            initializerAllDependencies.remove(initializer)
            initializers.values.forEach {
                it.remove(initializer)
            }
            initializerAllDependencies.values.forEach {
                it.remove(initializer)
            }
        }
    }

    private fun log(obj:Any){
        if(BuildConfig.DEBUG){
            Log.i(this.javaClass.simpleName, "${obj}")
        }
    }


    private fun assembleInitializerDependencies(){
        try {
            val provider = ComponentName(context.packageName, InitializationProvider::class.java.name)
            val providerInfo = context.packageManager.getProviderInfo(provider, PackageManager.GET_META_DATA)
            val metadata = providerInfo.metaData
            val startup = context.getString(R.string.eju_startup)
            if (metadata != null) {
                /**
                 * 已经过滤掉重复注册的Initializer
                 */
                val keys = metadata.keySet()
                /**
                 * 保存所有定义的Initializer(包含在meta-data中未定义的Initializer),和Initializer依赖的initializers
                 */
                val initializers: HashMap<Initializer<*>, HashSet<Initializer<*>>> = hashMapOf()

                for (key in keys) {
                    val value = metadata.getString(key, null)
                    if (startup == value) {
                        val clazz = Class.forName(key)
                        if (Initializer::class.java.isAssignableFrom(clazz)) {
                            val component = clazz as Class<out Initializer<*>>
                            val initializer :Initializer<*> = getOrCreateInitializerInstance(component)
                            assembleDependencies(initializer,initializers)
                        }
                    }
                }
                initializerAllDependencies.clear()
                assembleInitializerDependencies(initializers)
            }
        } catch (exception: Throwable) {
            throw StartupException(exception)
        }
    }


    private fun assembleDependencies(initializer :Initializer<*>,
                                     initializers: HashMap<Initializer<*>, HashSet<Initializer<*>>>
    ){
        initializers[initializer] =  initializer.dependencies().map { getOrCreateInitializerInstance(it)  }.toHashSet()
    }


    private fun getOrCreateInitializerInstance(component:Class<out Initializer<*>>):Initializer<*>{
        return createdInitializers[component.name]?:component.getDeclaredConstructor().newInstance().also {
            createdInitializers[component.name] = it
        }
    }


    private fun assembleInitializerDependencies(
        initializers: HashMap<Initializer<*>, HashSet<Initializer<*>>>
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