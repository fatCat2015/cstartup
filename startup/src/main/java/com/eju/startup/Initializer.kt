package com.eju.startup

import android.content.Context
import java.lang.Exception

/**
 * [Initializer]s can be used to initialize libraries during app startup, without
 * the need to use additional [android.content.ContentProvider]s.
 *
 * @param <T> The instance type being initialized
</T> */
interface Initializer<T> {
    /**
     * Initializes and a component given the application [Context]
     *
     * default: run on work thread
     *
     * @param context The application context.
     */
    fun create(context: Context):T

    /**
     * @return A list of dependencies that this [Initializer] depends on. This is
     * used to determine initialization order of [Initializer]s.
     * <br></br>
     * For e.g. if a [Initializer] `B` defines another
     * [Initializer] `A` as its dependency, then `A` gets initialized before `B`.
     */
    fun dependencies(): List<Class<out Initializer<*>>>

    /**
     * return true means it will auto create when InitializationProvider init
     */
    fun autoCreated():Boolean = true

    /**
     * return true means create() called on main thread
     */
    fun createOnMainThread():Boolean = false

    /**
     * called on throw Exception when create
     */
    fun onFailed(e:Exception){

    }

}