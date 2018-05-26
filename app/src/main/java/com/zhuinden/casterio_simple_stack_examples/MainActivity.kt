package com.zhuinden.casterio_simple_stack_examples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import com.zhuinden.simplestack.*
import kotlinx.android.synthetic.main.activity_main.*

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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        backstackDelegate.persistViewToState(container.getChildAt(0))
    }

    override fun onBackPressed() {
        if(!backstackDelegate.onBackPressed()) { // calls backstack.goBack()
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

        // we must generally ensure that the following scenarios work as expected:
        // [A, B] -> [A, B, C]
        // [A, B, C] -> [A, B]
        // [A, B, C] -> [D]

        // steps:
        // 1.) inflate the new view
        // 2.) persist state of the current view (one in the container)
        // 3.) restore the state of the new view (f.ex. back nav)
        // 4.) add the new view to container
        // 5.) wait for measure, animate transition
        // 6.) remove current view
        val newKey = stateChange.topNewState<ViewKey>()
        val newView = LayoutInflater.from(stateChange.createContext(this, newKey))
            .inflate(newKey.layoutId, container, false)

        val previousView = container.getChildAt(0)
        backstackDelegate.persistViewToState(previousView)
        backstackDelegate.restoreViewFromState(newView)

        container.addView(newView)

        if(previousView == null) {
            completionCallback.stateChangeComplete()
            return
        }

        val direction = stateChange.direction
        newView.waitForMeasure { view, width, height ->
            animateTogether(*when {
                direction == StateChange.REPLACE -> {
                    arrayOf(previousView.animateFadeOut(), newView.animateFadeIn())
                }
                else -> { // backward, forward
                    arrayOf(previousView.animateTranslateOut(width, direction),
                        newView.animateTranslateIn(width, direction))
                }
            }).onAnimationEnd {
                container.removeView(previousView)
                completionCallback.stateChangeComplete()
            }.start()
        }
    }
}
