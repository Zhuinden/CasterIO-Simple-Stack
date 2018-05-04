package com.zhuinden.casterio_simple_stack_examples

import android.support.v4.app.Fragment
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirstKey(val tag: String) : FragmentKey() {
    constructor() : this(FirstKey::class.java.name)

    override fun createFragment(): Fragment = FirstFragment()
}