package com.eju.startup

import androidx.annotation.RestrictTo
import java.lang.RuntimeException

/**
 * The Runtime Exception thrown by the android.startup library.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class StartupException : RuntimeException {
    constructor(message: String) : super(message) {}
    constructor(throwable: Throwable) : super(throwable) {}
    constructor(message: String, throwable: Throwable) : super(message, throwable) {}
}