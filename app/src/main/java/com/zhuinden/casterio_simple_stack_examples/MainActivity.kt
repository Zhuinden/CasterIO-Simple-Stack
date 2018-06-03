package com.zhuinden.casterio_simple_stack_examples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestack.navigator.DefaultStateChanger
import com.zhuinden.simplestack.navigator.Navigator
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk15.listeners.onClick

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity(), StateChanger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(false)
        }

        Navigator.configure()
            .setStateChanger(DefaultStateChanger.configure()
                .setExternalStateChanger(this)
                .create(this, container))
            .install(this, container, History.of(FirstKey()))

        buttonToolbarUp.onClick {
            backstack.goBack()
        }
    }

    override fun onBackPressed() {
        if(!backstack.goBack()) {
            super.onBackPressed()
        }
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        // this is where we handle navigation events.
        // when the navigation event change is handled, the completion callback must be called.

        // if the current state is already visible,
        // we *typically* don't need to manipulate what's active.
        if(stateChange.topNewState<Any>() == stateChange.topPreviousState()) {
            completionCallback.stateChangeComplete()
            return
        }

        // now everything is actually handled for us by DefaultStateChanger.
        // but we might want to set things here, like toolbar text, up arrow visibility, etc.
        // so we still need to set this as a state changer.
        // we can set this as an "external state changer" on the DefaultStateChanger.
        val newState = stateChange.getNewState<ViewKey>()
        val topKey = stateChange.topNewState<ViewKey>()
        textToolbar.setText(topKey.toolbarText)

        buttonToolbarUp.showIf { newState.size > 1 }
        completionCallback.stateChangeComplete()
    }
}
