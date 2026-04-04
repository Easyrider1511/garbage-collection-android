package com.garbagecollection.app.testsupport

import android.app.Activity
import org.robolectric.android.controller.ActivityController

inline fun <T : Activity> ActivityController<T>.useActivity(block: (T) -> Unit) {
    setup()
    try {
        block(get())
    } finally {
        destroy()
    }
}
