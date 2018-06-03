package com.zhuinden.casterio_simple_stack_examples

import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SecondKey(val clazz: String) : ViewKey() {
    override val layoutId: Int
        get() = R.layout.view_second

    @IgnoredOnParcel
    override val toolbarText: Int
        get() = R.string.second_view_toolbar_text

    constructor() : this(SecondKey::class.java.name)
}