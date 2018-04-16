package org.trustnote.wallet.js

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import org.trustnote.wallet.util.Utils

class TWebView : WebView {

    constructor(context: Context) : super(context) {
        initInternal()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initInternal()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initInternal()
    }

    private fun initInternal() {
        sInstance = this
        setupWebView()
        loadJS()
    }

    private fun loadJS() {
        //TODO: make sure JS load finished before call jsapi.
        val jsStream = context.assets.open("core.js")
        val jsString = jsStream.bufferedReader().use { it.readText() }
        callJS(jsString, ValueCallback {
            //Do nothing.
        })
    }

    inner class TWebChromeClient : WebChromeClient() {
        override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
            Utils.debugJS(cm.message() + " -- From line "
                    + cm.lineNumber() + " of "
                    + cm.sourceId());
            return true
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun setupWebView() {
        settings.javaScriptEnabled = true
        CookieManager.getInstance().setAcceptCookie(true)
        webChromeClient = TWebChromeClient()
    }

    fun callJS(jsCode: String, cb: ValueCallback<String>) {

        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            Utils.debugJS("JS should called from UI thread")
            Utils.crash("JS should called from UI thread")
            return
        }

        evaluateJavascript(jsCode) { valueFromJS ->
            cb.onReceiveValue(valueFromJS)
            Toast.makeText(context, valueFromJS, Toast.LENGTH_SHORT).show()
            //TODO:
            //I can use valueFromJS here
            //android.util.Log.e(TAG, valueFromJS); // prints the value
        }
    }

    companion object {

        lateinit var sInstance: TWebView

        fun init(context: Context) {
            sInstance = TWebView(context)
        }
    }

}


