package org.trustnote.wallet.js;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.trustnote.wallet.util.Utils;

public class TJSObject {

    public static Context sContext;
    @JavascriptInterface
    public void notify(String msg) {
        Utils.debugToast("NOTIFY from JS: " + msg);
    }

}