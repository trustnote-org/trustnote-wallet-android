package org.trustnote.wallet.util

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.js.BIP38_WORD_LIST_EN
import org.trustnote.wallet.uiframework.ActivityBase
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.webkit.WebView
import android.widget.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import org.trustnote.wallet.ActivityStarterChooser
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.FragmentDialogBase
import org.trustnote.wallet.biz.home.FragmentDialogCreateObserverFinish
import org.trustnote.wallet.extensions.inflateLayout
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.uiframework.FragmentBaseForHomePage
import org.trustnote.wallet.widget.ClearableEditText
import org.trustnote.wallet.widget.CustomViewFinderScannerActivity
import org.trustnote.wallet.widget.RecyclerItemClickListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AndroidUtils {

    const val KEY_BUNDLE_TITLE: String = "KEY_TITLE"
    const val KEY_BUNDLE_MSG: String = "KEY_MSG"
    const val KEY_BUNDLE_QRCODE: String = "KEY_BUNDLE_QRCODE"
    const val KEY_BUNDLE_ADDRESS: String = "KEY_BUNDLE_ADDRESS"
    const val KEY_BUNDLE_MEMO: String = "KEY_BUNDLE_MEMO"
    const val KEY_TAG_FOR_NEXT_PAGE: String = "KEY_TAG_FOR_NEXT_PAGE"
    const val KEY_CORRESPODENT_ADDRESSES: String = "KEY_CORRESPODENT_ADDRESSES"
    const val KEY_FROM_CHANGE_LANGUAGE: String = "KEY_FROM_CHANGE_LANGUAGE"
    const val KEY_FROM_SHARE_API: String = "KEY_FROM_SHARE_API"
    const val KEY_SHARE_TEXT: String = "KEY_SHARE_TEXT"

    const val KEY_SETTING_PAGE_TYPE: String = "KEY_SETTING_PAGE_TYPE"
    const val KEY_SETTING_PAGE_TITLE: String = "KEY_SETTING_PAGE_TITLE"
    const val KEY_WAITING_MSG_RES_ID: String = "KEY_WAITING_MSG_RES_ID"

    fun getTagForNextPage(bundle: Bundle?): Int {
        if (bundle != null) {
            val res = bundle.getString(KEY_TAG_FOR_NEXT_PAGE)?.toInt()
            return res ?: 0
        }
        return 0
    }

    fun getStringFromBundle(arguments: Bundle?, key: String): String {
        return if (arguments != null) {
            arguments.getString(key) ?: ""
        } else {
            ""
        }
    }

    fun getQrcodeFromBundle(bundle: Bundle?): String {
        val res = bundle?.getString(KEY_BUNDLE_QRCODE)
        return res ?: ""
    }

    fun getTitleFromBundle(bundle: Bundle): String {
        val res = bundle.getString(KEY_BUNDLE_TITLE)
        return res ?: ""
    }

    fun getMsgFromBundle(bundle: Bundle): String {
        val res = bundle.getString(KEY_BUNDLE_MSG)
        return res ?: ""
    }

    fun readAssetFile(fileName: String): String {
        val jsStream = TApp.context.assets.open(fileName)
        return jsStream.bufferedReader().use { it.readText() }
    }

    fun hideStatusBar(activity: ActivityBase, isShow: Boolean) {

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

    fun disableBtn(btn: TextView) {
        btn.alpha = 0.20f
        btn.isEnabled = false
    }

    fun enableBtn(btn: TextView, enable: Boolean) {
        if (enable) {
            enableBtn(btn)
        } else {
            disableBtn(btn)
        }
    }

    fun enableBtn(btn: TextView) {
        btn.alpha = 1f
        btn.isEnabled = true
    }

    fun startActivity(clz: Class<out ActivityBase>) {
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

    fun setupWarningWebView(webView: WebView, vararg warnings: Int) {
        val stringList = warnings.map {
            TApp.getString(it)
        }
        setupWarningWebView(webView, stringList)
    }

    fun setupWarningWebView(webView: WebView, items: List<String>) {
        val newList = items.map {
            """<li class="str">$it</li>"""
        }

        val data = AndroidUtils.readAssetFile("html_list.html")

        val localData = data.replace("WILL_REPLACE_PROGRAMATICALLY", newList.joinToString(" ") {
            it
        })

        webView.loadDataWithBaseURL("", localData, "text/html", "UTF-8", "")

    }

    @Throws(WriterException::class)
    fun encodeStrAsQrBitmap(str: String, size: Int): Bitmap {
        val result: BitMatrix
        val width = size
        try {
            val hints = EnumMap<EncodeHintType, Object>(EncodeHintType::class.java)
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8" as Object)
            hints.put(EncodeHintType.MARGIN, 0 as Object)
            result = MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, width, width, hints)
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
        f.show(AndroidUtils.getDialogFragmentTransaction(activity), null)
    }

    fun openDialog(activity: FragmentActivity, layoutId: Int, lambda: () -> Unit = {}, addToBackStack: Boolean = true) {
        val f = FragmentDialogBase(layoutId)
        f.show(AndroidUtils.getDialogFragmentTransaction(activity, addToBackStack), null)
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

    private fun bundleWithKeyValue(b: Bundle, key: String, value: String): Bundle {
        b.putString(key, value)
        return b
    }

    fun addFragmentArguments(f: Fragment, key: String, value: String): Fragment {

        var b = f.arguments
        if (b == null) {
            b = Bundle()
        }

        bundleWithKeyValue(b, key, value)
        f.arguments = b
        return f

    }

    fun copyTextToClipboard(myText: String) {

        val clipboard = TApp.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(TTT.KEY_TTT_QR_TAG, myText)
        clipboard.setPrimaryClip(clip)

    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = TApp.resources.getIdentifier("status_bar_height", "dimen", "android")

        if (resourceId > 0) {
            result = TApp.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
        val alphaAnimation = if (visibility == View.VISIBLE)
            AlphaAnimation(0f, 1f)
        else
            AlphaAnimation(1f, 0f)

        alphaAnimation.duration = duration
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }

    fun addItemClickListenerForRecycleView(recyclerView: RecyclerView, lambda: (position: Int) -> Unit) {
        recyclerView.addOnItemTouchListener(

                RecyclerItemClickListener(recyclerView.context,
                        recyclerView,
                        object : RecyclerItemClickListener.OnItemClickListener {
                            override fun onItemClick(view: View, position: Int) {
                                lambda.invoke(position)
                            }

                            override fun onLongItemClick(view: View, position: Int) {

                            }
                        })
        )
    }

    fun todo() {
        Utils.toastMsg(TApp.context.getString(R.string.coming_soon))
    }

    fun md5(s: String): String {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest = java.security.MessageDigest.getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            val hexString = StringBuilder()
            for (b in messageDigest) {

                val h = String.format("%02X", b)
                hexString.append(h)
            }
            return hexString.toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""

    }

    fun enableBtnIfTextViewIsNotEmpty(t: TextView, btn: Button) {
        val s = t.text.toString()
        if (s.trim().isBlank()) {
            AndroidUtils.disableBtn(btn)
        } else {
            AndroidUtils.enableBtn(btn)
        }
    }

    fun showErrIfInvalidInput(input: TextView, err: TextView, checkLogic: (s: String) -> Boolean) {
        if (checkLogic.invoke(input.text.toString())) {
            err.visibility = View.INVISIBLE
        } else {
            err.visibility = View.VISIBLE
        }
    }

    fun showWithAnimation(view: View, animation: Animation) {
        animation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {
                // TODO Auto-generated method stub

            }

            override fun onAnimationRepeat(animation: Animation) {
                // TODO Auto-generated method stub

            }

            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.VISIBLE
            }
        })

        view.setAnimation(animation)
        animation.start()
    }

    fun hidewWithAnimation(view: View, animation: Animation) {
        animation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {
                // TODO Auto-generated method stub

            }

            override fun onAnimationRepeat(animation: Animation) {
                // TODO Auto-generated method stub

            }

            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.INVISIBLE
            }
        })

        view.setAnimation(animation)
        animation.start()

    }

    fun hideErrIfHasFocus(editText: ClearableEditText, errView: View) {
        editText.bindingErr = errView
    }

    fun setLanguage(lang: String, activity: Activity) {

        Prefs.writeDefaultLanguage(lang)

        val myLocale = Locale(lang)
        Locale.setDefault(myLocale)

        val dm = TApp.resources.getDisplayMetrics()
        val conf = TApp.resources.getConfiguration()
        conf.locale = myLocale
        TApp.resources.updateConfiguration(conf, dm)

        val refresh = Intent(activity, ActivityStarterChooser::class.java)
        refresh.putExtra(AndroidUtils.KEY_FROM_CHANGE_LANGUAGE, true)

        activity.startActivity(refresh)
        activity.finish()
    }

    @JvmStatic
    fun isZh(context: Context): Boolean {
        return "zh" == context.resources.configuration.locale?.language
    }

    fun setLanguage(lang: String) {
        val myLocale = Locale(lang)
        Locale.setDefault(myLocale)

        val dm = TApp.resources.getDisplayMetrics()
        val conf = TApp.resources.getConfiguration()
        conf.locale = myLocale
        TApp.resources.updateConfiguration(conf, dm)
    }

    fun openSystemBrowser(urlWithHttp: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlWithHttp))
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        TApp.context.startActivity(browserIntent)
    }

    fun changeIconSizeForBottomNavigation(bottomNavigationView: BottomNavigationView) {
        val menuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        for (i in 0..(menuView.childCount - 1)) {
            val iconView = menuView.getChildAt(i).findViewById<ImageView>(android.support.design.R.id.icon)
            val layoutParams = iconView.layoutParams
            val width = TApp.resources.getDimensionPixelSize(R.dimen.line_gap_28)
            val height = TApp.resources.getDimensionPixelSize(R.dimen.line_gap_26)
            layoutParams.width = width
            layoutParams.height = height
            iconView.scaleType = ImageView.ScaleType.FIT_XY
            iconView.layoutParams = layoutParams
        }
    }

    fun handleScanResult(data: Intent?, scanResHandler: (String) -> Unit) {
        val result = CustomViewFinderScannerActivity.parseScanResult(data)
        if (result.isNotEmpty()) {
            scanResHandler.invoke(result ?: "")
        }
    }

    fun initiateScan(fragment: Fragment) {
        when (fragment) {
            is FragmentBase -> fragment.launchScanActivity()
            is FragmentBaseForHomePage -> fragment.launchScanActivity()
            is FragmentDialogBase -> fragment.launchScanActivity()
        }
    }

    fun showIosToast(message: String) {

        val toastView = TApp.context.inflateLayout(R.layout.l_toast_ios)
        toastView.findViewById<TextView>(R.id.message).text = message
        val toast = Toast(TApp.context)
        toast.view = toastView
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show()
    }

    fun getScreenWidth(activity: FragmentActivity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun openDefaultBrowser(activity: Activity, url: String) {
        var newUrl = url
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            newUrl = "http://$url"
        }

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newUrl))
        activity.startActivity(browserIntent)
    }
}

