package org.trustnote.wallet.util


import android.widget.Toast

import org.trustnote.wallet.TApp
import org.trustnote.wallet.network.hubapi.HubClient

import timber.log.Timber

object Utils {
    fun debugLog(s: String) {
        android.util.Log.e("GUO", s)
    }

    fun debugJS(s: String) {
        android.util.Log.e("JSAPI", s)
    }

    fun crash(s: String) {
        //TODO:
        android.util.Log.e("CRASH", s)
        throw RuntimeException(s)
    }

    fun debugToast(s: String) {
        Toast.makeText(TApp.context, s, Toast.LENGTH_SHORT).show()
    }

    fun toastMsg(s: String) {
        Toast.makeText(TApp.context, s, Toast.LENGTH_SHORT).show()
    }

    fun toastMsg(stringResId: Int) {
        Toast.makeText(TApp.context, stringResId, Toast.LENGTH_SHORT).show()
    }

    fun d(clz: Class<*>, msg: String) {
        Timber.d(clz.simpleName + msg)
    }

    //TODO: Bug?? Thread Manager
    fun computeThread(action: () -> Any) {
        Thread {
            action
        }.start()
    }

    //TODO:
    fun runInbackground(runnable: Runnable) {
        Thread { runnable.run() }.start()
    }

}

