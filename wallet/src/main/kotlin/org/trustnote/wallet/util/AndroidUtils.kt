package org.trustnote.wallet.util

import android.app.Activity
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.os.Build
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


object AndroidUtils {
    fun hideStatusBar(activity: Activity, isShow: Boolean) {

        val uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility()
        var newUiOptions = uiOptions
        val isImmersiveModeEnabled = ((uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) === uiOptions)
        if (isImmersiveModeEnabled) {
            Utils.debugLog("Turning immersive mode mode off. ")
        } else {
            Utils.debugLog("Turning immersive mode mode on. ")
        }

        if (isImmersiveModeEnabled == isShow) {
            return
        }


        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions)
        //END_INCLUDE (set_ui_flags)
    }

}