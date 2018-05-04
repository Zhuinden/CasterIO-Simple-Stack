package com.zhuinden.casterio_simple_stack_examples

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment

abstract class FragmentKey: Parcelable {
    open val fragmentTag: String
        get() = toString()

    protected abstract fun createFragment(): Fragment

    fun newFragment(): Fragment = createFragment().apply {
        arguments = (arguments ?: Bundle()).apply {
            putParcelable("KEY", this@FragmentKey)
        }
    }
}

fun <T: FragmentKey> Fragment.getKey(): T = arguments!!.getParcelable("KEY")