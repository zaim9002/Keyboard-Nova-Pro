package com.example.ime

import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

abstract class ComposeInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner,
    OnBackPressedDispatcherOwner {

    private val lifecycleRegistry by lazy { LifecycleRegistry(this) }
    private val store by lazy { ViewModelStore() }
    private val savedStateRegistryController by lazy { SavedStateRegistryController.create(this) }
    private val backDispatcher by lazy {
        OnBackPressedDispatcher {
            requestHideSelf(0)
        }
    }

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val onBackPressedDispatcher: OnBackPressedDispatcher get() = backDispatcher

    override fun onCreate() {
        super.onCreate()
        try {
            if (!savedStateRegistry.isRestored) {
                savedStateRegistryController.performRestore(Bundle())
            }
        } catch (e: Throwable) {
            Log.w("ComposeIME", "performRestore ignored: ${e.message}")
        }
        dispatchLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onEvaluateFullscreenMode(): Boolean = false
    override fun onEvaluateInputViewShown(): Boolean = true

    override fun onConfigureWindow(win: Window, isFullscreen: Boolean, isCandidatesOnly: Boolean) {
        super.onConfigureWindow(win, isFullscreen, isCandidatesOnly)
        try {
            win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            win.setGravity(Gravity.BOTTOM)
            win.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            win.decorView.let { decorView ->
                decorView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                decorView.setViewTreeLifecycleOwner(this)
                decorView.setViewTreeViewModelStoreOwner(this)
                decorView.setViewTreeSavedStateRegistryOwner(this)
                decorView.setViewTreeOnBackPressedDispatcherOwner(this)
                (decorView as? ViewGroup)?.let { group ->
                    for (i in 0 until group.childCount) {
                        val child = group.getChildAt(i)
                        (child as? android.widget.LinearLayout)?.let { linear ->
                            linear.gravity = Gravity.BOTTOM
                            linear.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        }
                    }
                }
                decorView.findViewById<View>(android.R.id.inputArea)?.let { inputArea ->
                    inputArea.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    (inputArea.parent as? android.widget.LinearLayout)?.let { parentLinear ->
                        parentLinear.gravity = Gravity.BOTTOM
                        parentLinear.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                    val lp = inputArea.layoutParams
                    if (lp is android.widget.LinearLayout.LayoutParams) {
                        lp.gravity = Gravity.BOTTOM
                        lp.weight = 0f
                        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        inputArea.layoutParams = lp
                    } else if (lp is FrameLayout.LayoutParams) {
                        lp.gravity = Gravity.BOTTOM
                        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        inputArea.layoutParams = lp
                    }
                }
            }
        } catch (e: Throwable) {
            Log.w("ComposeIME", "onConfigureWindow error: ${e.message}")
        }
    }

    protected var keyboardRootView: View? = null

    override fun onComputeInsets(outInsets: Insets) {
        super.onComputeInsets(outInsets)
        try {
            val decor = window?.window?.decorView ?: return
            val targetView = keyboardRootView
                ?: decor.findViewById<View>(android.R.id.inputArea)
                ?: decor
            val loc = IntArray(2)
            targetView.getLocationInWindow(loc)
            val decorHeight = decor.height
            val viewHeight = targetView.height

            val top = when {
                loc[1] > 0 -> loc[1]
                decorHeight > 0 && viewHeight > 0 -> (decorHeight - viewHeight).coerceAtLeast(0)
                decorHeight > 0 -> decorHeight
                else -> 0
            }
            outInsets.contentTopInsets = top
            outInsets.visibleTopInsets = top
            outInsets.touchableInsets = Insets.TOUCHABLE_INSETS_CONTENT
        } catch (e: Throwable) {
            Log.w("ComposeIME", "onComputeInsets error: ${e.message}")
        }
    }

    override fun onStartInput(attribute: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        ensureStarted()
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        ensureResumed()
    }

    override fun onWindowShown() {
        super.onWindowShown()
        ensureResumed()
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        ensurePaused()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        ensureStopped()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backDispatcher.hasEnabledCallbacks()) {
                backDispatcher.onBackPressed()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backDispatcher.hasEnabledCallbacks()) {
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        ensureStopped()
        dispatchLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        try {
            store.clear()
        } catch (e: Throwable) {
            Log.w("ComposeIME", "store clear error: ${e.message}")
        }
    }

    private fun ensureStarted() {
        if (lifecycleRegistry.currentState == Lifecycle.State.INITIALIZED) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }
        if (lifecycleRegistry.currentState == Lifecycle.State.CREATED) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_START)
        }
    }

    private fun ensureResumed() {
        ensureStarted()
        if (lifecycleRegistry.currentState == Lifecycle.State.STARTED) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    private fun ensurePaused() {
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
    }

    private fun ensureStopped() {
        ensurePaused()
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    private fun dispatchLifecycleEvent(event: Lifecycle.Event) {
        try {
            lifecycleRegistry.handleLifecycleEvent(event)
        } catch (e: Throwable) {
            Log.w("ComposeIME", "Lifecycle transition error on $event: ${e.message}")
        }
    }
}
