package com.zhuinden.casterio_simple_stack_examples

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import com.zhuinden.simplestack.*

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity(), StateChanger {
    private lateinit var backstackDelegate: BackstackDelegate
    lateinit var backstack: Backstack
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        backstackDelegate = BackstackDelegate()
        backstackDelegate.onCreate(savedInstanceState,
            lastCustomNonConfigurationInstance,
            History.single(FirstKey()))
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

    private fun FragmentManager.findFragment(key: FragmentKey) = findFragmentByTag(key.fragmentTag)
    private fun FragmentTransaction.addFragment(@IdRes containerId: Int, key: FragmentKey) {
        add(containerId, key.newFragment(), key.fragmentTag)
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        // this is where we handle navigation events.
        // when the navigation event change is handled, the completion callback must be called.

        // if the current state is already visible,
        // we *typically* don't need to manipulate what's active.
        if(stateChange.topNewState<Any>() == (stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete()
            return
        }
        // we must generally ensure that the following scenarios work as expected:
        // [A, B] -> [A, B, C]
        // [A, B, C] -> [A, B]
        // [A, B, C] -> [D]
        val fragmentManager = supportFragmentManager

        val newStates = stateChange.getNewState<FragmentKey>()
        val previousStates = stateChange.getPreviousState<FragmentKey>()
        val topNewState = stateChange.topNewState<FragmentKey>()

        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        // first we must ensure that all Previous items not in New are removed
        for(previousState in previousStates) {
            if(!newStates.contains(previousState)) {
                transaction.remove(fragmentManager.findFragment(previousState))
            }
        }
        // then we must ensure that the top is visible, and others are hidden.
        // if top exists but is hidden, then it should be shown. If doesn't exist, add it.
        for(newState in newStates) {
            val fragment = fragmentManager.findFragment(newState)
            if(newState == topNewState) {
                if(fragment == null) {
                    transaction.addFragment(R.id.container, newState)
                } else if(fragment.isHidden) {
                    transaction.show(fragment)
                }
            } else {
                if (fragment != null && !fragment.isHidden) {
                    transaction.hide(fragment)
                }
            }
        }
        transaction.commitAllowingStateLoss() // never can trust commit() :)

        completionCallback.stateChangeComplete()
    }
}
