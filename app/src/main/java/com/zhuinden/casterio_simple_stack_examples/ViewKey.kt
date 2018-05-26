package com.zhuinden.casterio_simple_stack_examples

import android.os.Parcelable

abstract class ViewKey: Parcelable {
    abstract val layoutId: Int
}