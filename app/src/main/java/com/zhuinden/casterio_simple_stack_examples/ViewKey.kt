package com.zhuinden.casterio_simple_stack_examples

import android.os.Parcelable
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler

abstract class ViewKey: StateKey, Parcelable {
    abstract val layoutId: Int

    abstract val toolbarText: Int

    // for StateKey (used by default in Navigator)
    override fun layout(): Int = layoutId
    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
}