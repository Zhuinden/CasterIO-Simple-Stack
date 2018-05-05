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

    fun FragmentTransaction.addFragment(@IdRes containerId: Int, key: FragmentKey) {
        add(containerId, key.newFragment(), key.fragmentTag)
    }

    fun FragmentManager.findFragment(key: FragmentKey) = findFragmentByTag(key.fragmentTag)

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        // this is where we handle navigation events.
        // when the navigation event change is handled, the completion callback must be called.

        // if the current state is already visible,
        // we *typically* don't need to manipulate what's active.
        if(stateChange.topNewState<Any>() == stateChange.topPreviousState()) {
            completionCallback.stateChangeComplete()
            return
        }

        // we must generally ensure that the following scenarios work as expected:
        // [A, B] -> [A, B, C]
        // [A, B, C] -> [A, B]
        // [A, B, C] -> [D]

        // for this, we must track what Fragments exist in the backstack.

        val fragmentManager = supportFragmentManager

        val newStates = stateChange.getNewState<FragmentKey>()
        val previousStates = stateChange.getPreviousState<FragmentKey>()
        val topNewState = stateChange.topNewState<FragmentKey>()

        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE) // fade anim

        // first, we must remove fragments that are no longer in the stack.
        for(previousState in previousStates) {
            val fragment = fragmentManager.findFragment(previousState)
            if(fragment != null && !newStates.contains(previousState)) {
                transaction.remove(fragment)
            }
        }

        // then, we must hide the non-top fragments that are in the manager.
        // if the new top doesn't exist, we should create it.
        // if it is hidden, we should show it.
        for(newState in newStates) {
            val fragment = fragmentManager.findFragment(newState)
            if(newState == topNewState) {
                if(fragment == null) {
                    transaction.addFragment(R.id.container, newState)
                } else if(fragment.isHidden) {
                    transaction.show(fragment)
                }
            } else { // fragment that exists, but isn't currently showing. They should be hidden.
                if(fragment != null && !fragment.isHidden) {
                    transaction.hide(fragment)
                }
            }
        }

        transaction.commitAllowingStateLoss() // never trust commit() :)

        completionCallback.stateChangeComplete()
    }
}
