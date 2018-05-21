package org.trustnote.wallet.util

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.Button
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.js.BIP38_WORD_LIST_EN
import org.trustnote.wallet.uiframework.BaseActivity
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.webkit.WebView

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

//    fun resizeDrawable(drawableResId: Int, sizeResId: Int): Drawable {
//        val size = TApp.context.resources.getDimension(sizeResId).toInt()
//        val res = TApp.context.resources.getDrawable(drawableResId)
//        res.setBounds(0, 0, size, size)
//        return res
//    }

    fun resizeDrawable(drawableResId: Int, sizeResId: Int): Drawable {
        val size = TApp.context.resources.getDimension(sizeResId).toInt()
        val res = TApp.context.resources.getDrawable(drawableResId)
        val bitmapResized = Bitmap.createScaledBitmap((res as BitmapDrawable).bitmap, size, size, false)
        return BitmapDrawable(TApp.context.resources, bitmapResized)
    }

    fun getString(strResId: Int): String {
        return TApp.context.resources.getString(strResId)
    }

    fun setupWarningWebView(webView: WebView, tag: String) {
        val data = AndroidUtils.readAssetFile("pwd_warning.html")
        val localData = AndroidUtils.replaceTTTTag(data, tag)
        webView.loadDataWithBaseURL("", localData, "text/html", "UTF-8", "")
    }


    fun replaceTTTTag(html:String, tag: String):String {
        return html.replace("TTTTAG(.*)TTTTAG".toRegex()){
            val strResName = tag + it.groupValues[1]
            val resId = TApp.context.resources.getIdentifier(strResName, "string", TApp.context.packageName)
            TApp.context.getString(resId)
        }
    }


}