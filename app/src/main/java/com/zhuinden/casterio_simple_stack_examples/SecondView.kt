package com.zhuinden.casterio_simple_stack_examples

import android.content.Context
import android.support.annotation.AttrRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.zhuinden.simplestack.Backstack

class SecondView : ConstraintLayout {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?,
                @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context) {
        // do something
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val secondKey = Backstack.getKey<SecondKey>(context)
    }
}