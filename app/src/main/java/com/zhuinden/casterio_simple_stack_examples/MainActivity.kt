package com.zhuinden.casterio_simple_stack_examples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zhuinden.simplestack.*

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity(), StateChanger {
    private lateinit var backstackDelegate: BackstackDelegate
    private lateinit var backstack: Backstack

    override fun onCreate(savedInstanceState: Bundle?) {
        backstackDelegate = BackstackDelegate()
        backstackDelegate.onCreate(savedInstanceState,
            lastCustomNonConfigurationInstance,
            History.single(TODO()))
        backstackDelegate.registerForLifecycleCallbacks(this)

        backstack = backstackDelegate.backstack

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        backstackDelegate.setStateChanger(this)
    }

    override fun onRetainCustomNonConfigurationInstance() =
        backstackDelegate.onRetainCustomNonConfigurationInstance()

    override fun onBackPressed() {
        if(!backstackDelegate.onBackPressed()) { // calls backstack.goBack()
            super.onBackPressed()
        }
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        // this is where we handle navigation events.
        // when the navigation event change is handled, the completion callback must be called.
        completionCallback.stateChangeComplete()
    }
}
