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

    override fun onBackPressed() {
        if (!backstackDelegate.onBackPressed()) { // calls backstack.goBack()
            super.onBackPressed()
        }
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        // this is where we handle navigation events.
        // when the navigation event change is handled, the completion callback must be called.

        // if the current state is already visible,
        // we *typically* don't need to manipulate what's active.
        if (stateChange.topNewState<Any>() == stateChange.topPreviousState()) {
            completionCallback.stateChangeComplete()
            return
        }

        // we must generally ensure that the following scenarios work as expected:
        // [A, B] -> [A, B, C]
        // [A, B, C] -> [A, B]
        // [A, B, C] -> [D]

        // let's replace the Fragment-based implementation with Compound Viewgroups!

        // steps:
        // 1.) inflate the new view
        // 2.) persist state of the current view
        // 3.) restore the state of the new view (f.ex. back nav)
        // 4.) add the new view to container
        // 5.) animate transition
        // 6.) remove current view
        val newKey = stateChange.topNewState<ViewKey>()
        val inflater = LayoutInflater.from(stateChange.createContext(this, newKey))

        val previousView = container.getChildAt(0)
        val newView = inflater.inflate(newKey.layoutId, container, false)
        backstackDelegate.restoreViewFromState(newView)
        backstackDelegate.persistViewToState(previousView)
        container.addView(newView)

        val direction = stateChange.direction
        if (previousView == null) {
            completionCallback.stateChangeComplete()
            return
        }

        newView.waitForMeasure { view, width, height ->
            if (direction == StateChange.REPLACE) {
                animateTogether(
                    newView.animateFadeIn(),
                    previousView.animateFadeOut()
                ).apply {
                    addListener(createAnimatorListener(
                        onAnimationEnd = {
                            container.removeView(previousView)
                            completionCallback.stateChangeComplete()
                        }
                    ))
                }.start()
            } else {
                animateTogether(
                    newView.animateTranslateXBy(
                        from = direction * width,
                        by = (-1) * direction * width
                    ),
                    previousView.animateTranslateXBy(
                        from = 0,
                        by = (-1) * direction * width
                    )
                ).apply {
                    addListener(createAnimatorListener(
                        onAnimationEnd = {
                            container.removeView(previousView)
                            completionCallback.stateChangeComplete()
                        }
                    ))
                }.start()
            }
        }
    }
}
