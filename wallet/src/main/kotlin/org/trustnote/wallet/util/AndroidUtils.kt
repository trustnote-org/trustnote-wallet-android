package org.trustnote.wallet.util

import android.content.Intent
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.Button
import org.trustnote.wallet.MAINACTIVITY_KEY_MENU_ID
import org.trustnote.wallet.MainActivity
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.wallet.CreateWalletFragment
import org.trustnote.wallet.js.BIP38_WORD_LIST_EN
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.widget.MyDialogFragment
import android.support.v4.content.ContextCompat
import android.view.WindowManager






object AndroidUtils {

    fun readAssetFile(fileName: String): String {
        val jsStream = TApp.context.assets.open(fileName)
        return jsStream.bufferedReader().use { it.readText() }
    }


    fun hideStatusBar(activity: BaseActivity, isShow: Boolean) {

        val uiOptions = activity.window.decorView.systemUiVisibility
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
        //        if (Build.VERSION.SDK_INT >= 14) {
        //            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        //        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        activity.window.decorView.systemUiVisibility = newUiOptions
        //END_INCLUDE (set_ui_flags)
    }


    fun replaceTTTTag(html:String):String {

        return html.replace("TTTTAG(.*)TTTTAG".toRegex()){
            val strResName = it.groupValues[1]
            val resId = TApp.context.resources.getIdentifier(strResName, "string", TApp.context.packageName)
            TApp.context.getString(resId)
        }
    }

    fun disableBtn(btn: Button) {
        btn.alpha = 0.5f
        btn.isEnabled = false
    }

    fun enableBtn(btn: Button, enable: Boolean) {
        if (enable) {
            enableBtn(btn)
        } else {
            disableBtn(btn)
        }
    }

    fun enableBtn(btn: Button) {
        btn.alpha = 1f
        btn.isEnabled = true
    }


    fun startActivity(clz: Class<out BaseActivity>) {
        val intent = Intent(TApp.context, clz)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        TApp.context.startActivity(intent)
    }

    fun readBip38List() {
        val s = readAssetFile("bip38wordlist_en.txt")
        BIP38_WORD_LIST_EN = s.split("\n")
    }
}