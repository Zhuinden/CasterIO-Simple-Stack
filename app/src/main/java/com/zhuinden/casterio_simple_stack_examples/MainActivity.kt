package com.zhuinden.casterio_simple_stack_examples

import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.zhuinden.simplestack.*
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk15.listeners.onClick

class MainActivity : AppCompatActivity(), StateChanger {
    @Parcelize
    data class Screen(val screen: String) : Parcelable

    private lateinit var backstackDelegate: BackstackDelegate
    private lateinit var backstack: Backstack

    override fun onCreate(savedInstanceState: Bundle?) {
        backstackDelegate = BackstackDelegate()
        backstackDelegate.onCreate(savedInstanceState,
            lastCustomNonConfigurationInstance,
            History.single(Screen("FIRST")))
        backstackDelegate.registerForLifecycleCallbacks(this)

        backstack = backstackDelegate.backstack

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupClicks()

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
        Log.i("MainActivity",
            "\nNavigation States:\n" +
                "Previous: [${stateChange.getPreviousState<Screen>().asString()}\n" +
                "New: ${stateChange.getNewState<Screen>().asString()}]\n")
        completionCallback.stateChangeComplete()
    }

    // handle button clicks
    private fun setupClicks() {
        goToScreenB.onClick {
            backstack.goTo(Screen("B"))
        }

        goToScreenC.onClick {
            backstack.goTo(Screen("C"))
        }

        setHistoryD.onClick {
            backstack.setHistory(History.single(Screen("D")), StateChange.REPLACE)
        }

        goUpToC.onClick {
            backstack.goUp(Screen("C"))
        }

        goUpChainToAB.onClick {
            backstack.goUpChain(History.of(Screen("A"), Screen("B")))
        }
    }
}
