package com.garbagecollection.app.testsupport

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.garbagecollection.app.R

class FragmentTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_GarbageCollection)
        super.onCreate(savedInstanceState)
    }
}
