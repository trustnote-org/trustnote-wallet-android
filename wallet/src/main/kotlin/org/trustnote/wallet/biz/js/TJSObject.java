package org.trustnote.wallet.biz.js;

import android.content.Context;
import android.webkit.JavascriptInterface;

import org.trustnote.wallet.util.Utils;

public class TJSObject {

    public static Context sContext;
    @JavascriptInterface
    public void notify(String msg) {
        Utils.INSTANCE.debugToast("NOTIFY from JS: " + msg);
    }

}