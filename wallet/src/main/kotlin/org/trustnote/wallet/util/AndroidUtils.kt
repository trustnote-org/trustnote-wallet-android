package org.trustnote.wallet.util

import android.app.Activity
import android.os.Build
import android.view.View
import org.trustnote.wallet.TApp


object AndroidUtils {

    fun readAssetFile(fileName: String): String {
        val jsStream = TApp.context.assets.open(fileName)
        return jsStream.bufferedReader().use { it.readText() }
    }


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


    fun replaceTTTTag(html:String):String {

        return html.replace("TTTTAG(.*)TTTTAG".toRegex()){
            val strResName = it.groupValues[1]
            val resId = TApp.context.resources.getIdentifier(strResName, "string", TApp.context.packageName)
            TApp.context.getString(resId)
        }
    }

}