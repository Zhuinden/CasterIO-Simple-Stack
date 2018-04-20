package com.zhuinden.casterio_simple_stack_examples

import java.util.*

inline fun <reified T> List<T>.asString() : String = Arrays.toString(this.toTypedArray())