package com.zhuinden.casterio_simple_stack_examples

import android.content.Context
import android.support.annotation.AttrRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.statebundle.StateBundle
import kotlinx.android.synthetic.main.view_first.view.*
import org.jetbrains.anko.sdk15.listeners.onClick

class FirstView : ConstraintLayout, Bundleable {
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
        goToSecondView.onClick {
            context.findActivity<MainActivity>().backstack.goTo(SecondKey())
        }
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        // persist state here
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            // restore state here
        }
    }
}