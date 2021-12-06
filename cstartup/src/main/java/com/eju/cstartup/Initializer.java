package com.eju.cstartup;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * {@link Initializer}s can be used to initialize libraries during app startup, without
 * the need to use additional {@link android.content.ContentProvider}s.
 *
 * @param <T> The instance type being initialized
 */
public interface Initializer<T> {

    /**
     * Initializes and a component given the application {@link Context}
     *
     * @param context The application context.
     */
    @NonNull
    T create(@NonNull Context context);

    /**
     * @return A list of dependencies that this {@link Initializer} depends on. This is
     * used to determine initialization order of {@link Initializer}s.
     * <br/>
     * For e.g. if a {@link Initializer} `B` defines another
     * {@link Initializer} `A` as its dependency, then `A` gets initialized before `B`.
     */
    @NonNull
    List<Class<? extends Initializer<?>>> dependencies();
}

