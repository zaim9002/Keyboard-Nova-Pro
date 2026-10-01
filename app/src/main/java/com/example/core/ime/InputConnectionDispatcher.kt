package com.example.core.ime

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class InputConnectionDispatcher(
    private val inputConnectionProvider: () -> InputConnection?,
    private val onCustomCommitFallback: ((String) -> Unit)? = null,
    private val onCustomDeleteFallback: (() -> Unit)? = null
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var repeatJob: Job? = null

    val currentInputConnection: InputConnection?
        get() = inputConnectionProvider()

    fun commitText(text: String, newCursorPosition: Int = 1) {
        val ic = currentInputConnection
        if (ic != null) {
            ic.commitText(text, newCursorPosition)
        } else {
            onCustomCommitFallback?.invoke(text)
        }
    }

    fun deleteBackspace() {
        val ic = currentInputConnection
        if (ic != null) {
            // First check if there is an active selection
            val selectedText = ic.getSelectedText(0)
            if (!selectedText.isNullOrEmpty()) {
                ic.commitText("", 1)
            } else {
                ic.deleteSurroundingText(1, 0)
            }
        } else {
            onCustomDeleteFallback?.invoke()
        }
    }

    fun startBackspaceRepeat(onFeedback: () -> Unit) {
        stopBackspaceRepeat()
        repeatJob = scope.launch {
            deleteBackspace()
            onFeedback()
            delay(400) // Initial delay before repeat starts

            var count = 0
            while (isActive) {
                deleteBackspace()
                onFeedback()
                count++
                // Accelerating speed ramp: starts at 60ms, ramps to 30ms
                val interval = when {
                    count > 25 -> 25L
                    count > 10 -> 40L
                    else -> 60L
                }
                delay(interval)
            }
        }
    }

    fun stopBackspaceRepeat() {
        repeatJob?.cancel()
        repeatJob = null
    }

    fun performImeAction(action: Int = EditorInfo.IME_ACTION_DONE) {
        val ic = currentInputConnection
        if (ic != null) {
            val handled = ic.performEditorAction(action)
            if (!handled) {
                // Fallback to sending enter key event
                sendKeyEvent(KeyEvent.KEYCODE_ENTER)
            }
        } else {
            commitText("\n")
        }
    }

    fun sendKeyEvent(keyCode: Int) {
        val ic = currentInputConnection
        if (ic != null) {
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        }
    }

    fun moveCursorLeft() {
        sendKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT)
    }

    fun moveCursorRight() {
        sendKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT)
    }

    fun moveCursorUp() {
        sendKeyEvent(KeyEvent.KEYCODE_DPAD_UP)
    }

    fun moveCursorDown() {
        sendKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN)
    }

    fun getTextBeforeCursor(n: Int = 30): CharSequence? {
        return currentInputConnection?.getTextBeforeCursor(n, 0)
    }
}
