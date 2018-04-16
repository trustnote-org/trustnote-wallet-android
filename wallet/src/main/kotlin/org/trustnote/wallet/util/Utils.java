package org.trustnote.wallet.util;


import android.widget.Toast;

import org.trustnote.wallet.TApp;

public class Utils {
    public static void debugLog(String s) {
        android.util.Log.e("GUO", s);
    }

    public static void debugJS(String s) {
        android.util.Log.e("JSAPI", s);
    }

    public static void crash(String s) {
        //TODO:
        android.util.Log.e("CRASH", s);
    }

    public static void debugToast(String s) {
        Toast.makeText(TApp.context, s, Toast.LENGTH_SHORT).show();
    }

    public static void toastMsg(String s) {
        Toast.makeText(TApp.context, s, Toast.LENGTH_SHORT).show();
    }

    public static void toastMsg(int stringResId) {
        Toast.makeText(TApp.context, stringResId, Toast.LENGTH_SHORT).show();
    }

}
