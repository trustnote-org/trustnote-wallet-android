package org.trustnote.wallet.widget.keyboard

import android.app.Activity
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener
import android.view.KeyEvent
import org.trustnote.wallet.uiframework.ActivityBase

class BasicOnKeyboardActionListener(private val mTargetActivity: Activity) : OnKeyboardActionListener {

    override fun swipeUp() {
        // TODO Auto-generated method stub

    }

    override fun swipeRight() {
        // TODO Auto-generated method stub

    }

    override fun swipeLeft() {
        // TODO Auto-generated method stub

    }

    override fun swipeDown() {
        // TODO Auto-generated method stub

    }

    override fun onText(text: CharSequence) {
        // TODO Auto-generated method stub

    }

    override fun onRelease(primaryCode: Int) {
        // TODO Auto-generated method stub

    }

    override fun onPress(primaryCode: Int) {
        // TODO Auto-generated method stub
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray) {
        val eventTime = System.currentTimeMillis()

        var newPrimaryCode = if (primaryCode > 0) primaryCode - 68 else primaryCode

        if (primaryCode == -3) {
            newPrimaryCode = KeyEvent.KEYCODE_ENTER
            (mTargetActivity as ActivityBase).hideKeyboardWithAnimation()
            return
        }

        if (primaryCode == -5) {
            newPrimaryCode = KeyEvent.KEYCODE_DEL
        }

        val event = KeyEvent(eventTime, eventTime,
                KeyEvent.ACTION_DOWN, newPrimaryCode, 0, 0, 0, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE)

        mTargetActivity.dispatchKeyEvent(event)
    }
}