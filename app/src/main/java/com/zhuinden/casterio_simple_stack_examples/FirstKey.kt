package com.zhuinden.casterio_simple_stack_examples

import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirstKey(val clazz: String) : ViewKey() {
    override val layoutId: Int
        get() = R.layout.view_first

    constructor() : this(FirstKey::class.java.name)
}