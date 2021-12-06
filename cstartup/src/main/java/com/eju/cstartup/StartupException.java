package com.eju.cstartup;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

/**
 * The Runtime Exception thrown by the android.startup library.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@SuppressWarnings("WeakerAccess")
public final class StartupException extends RuntimeException {
    public StartupException(@NonNull String message) {
        super(message);
    }

    public StartupException(@NonNull Throwable throwable) {
        super(throwable);
    }

    public StartupException(@NonNull String message, @NonNull Throwable throwable) {
        super(message, throwable);
    }
}