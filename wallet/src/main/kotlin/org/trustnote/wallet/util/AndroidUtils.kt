package org.trustnote.wallet.util

import android.app.Activity
import android.content.Context
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
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.WindowManager
import android.webkit.WebView
import android.widget.PopupWindow
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import org.trustnote.wallet.biz.FragmentDialogBase
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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

    fun resizeErrDrawable(drawableResId: Int, sizeResId: Int): Drawable {
        val size = TApp.context.resources.getDimension(sizeResId).toInt()
        val res = TApp.context.resources.getDrawable(drawableResId)
        res.setBounds(0, 0, size, size)
        return res
    }

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

    fun replaceTTTTag(html: String, tag: String): String {
        return html.replace("TTTTAG(.*)TTTTAG".toRegex()) {
            val strResName = tag + it.groupValues[1]
            val resId = TApp.context.resources.getIdentifier(strResName, "string", TApp.context.packageName)
            TApp.context.getString(resId)
        }
    }

    @Throws(WriterException::class)
    fun encodeStrAsQrBitmap(str: String, size: Int): Bitmap {
        val result: BitMatrix
        val width = size
        try {
            result = MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, width, width, null)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            // TODO: return ERR code.
            return Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        }

        val w = result.getWidth()
        val h = result.getHeight()
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (result.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h)
        return bitmap
    }

    ///            val p = Utils.popupDisplay(activity)
    //       p.showAsDropDown(button, -40, -780)

    public fun popupDisplay(activity: Activity): PopupWindow {

        val popupWindow = PopupWindow(activity);

        // inflate your layout or dynamically add view
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.item_field, null)

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow

    }

    val KEY_DIALOG_FRAGMENT_TRANSACTION = "dialog"

    fun getDialogFragmentTransaction(activity: FragmentActivity, addToBackStack: Boolean = true): FragmentTransaction {

        val ft = activity.supportFragmentManager.beginTransaction()
        val prev = activity.supportFragmentManager.findFragmentByTag(KEY_DIALOG_FRAGMENT_TRANSACTION)
        if (prev != null) {
            ft.remove(prev)
        }

        if (addToBackStack) {
            ft.addToBackStack(null)
        }
        return ft
    }

    fun openDialog(activity: FragmentActivity, f: FragmentDialogBase, addToBackStack: Boolean = true) {
        f.show(AndroidUtils.getDialogFragmentTransaction(activity), KEY_DIALOG_FRAGMENT_TRANSACTION)
    }

    fun openDialog(activity: FragmentActivity, layoutId: Int, lambda: () -> Unit = {}, addToBackStack: Boolean = true) {
        val f = FragmentDialogBase(layoutId)
        f.show(AndroidUtils.getDialogFragmentTransaction(activity, addToBackStack), KEY_DIALOG_FRAGMENT_TRANSACTION)
    }

    @Synchronized
    fun getMySdcardDirectory(): File {
        val pInfo = TApp.context.packageManager.getPackageInfo(TApp.context.packageName, 0)
        val versionName = pInfo.versionName
        val res = File(Environment.getExternalStorageDirectory(), "TTT_$versionName")
        if (!res.exists()) {
            res.mkdir()
        }
        return res
    }

    fun exportDataForDebug() {
        val source = TApp.context.filesDir.parentFile
        val simpleDateFormat = SimpleDateFormat("MM_HH___yyyy_MM_dd")
        val exportFolderName = simpleDateFormat.format(Date())

        val target = File(getMySdcardDirectory(), exportFolderName)
        source.copyRecursively(target, true)

        val fixedTarget = File(Environment.getExternalStorageDirectory(), "ttt_latest_copy")
        if (fixedTarget.exists()) {
            fixedTarget.delete()
        }
        source.copyRecursively(fixedTarget, true)

    }


    private fun bundleWithKeyValue(key: String, value: String):Bundle {
        val b = Bundle()
        b.putString(key, value)
        return b
    }

    fun addFragmentArguments(f: Fragment, key: String, value: String) {
        val b = bundleWithKeyValue(key, value)
        f.arguments = b
    }
}