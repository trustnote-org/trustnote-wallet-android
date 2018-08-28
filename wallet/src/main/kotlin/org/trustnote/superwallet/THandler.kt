package org.trustnote.superwallet

import android.os.Handler

class THandler : Handler(TApp.context.mainLooper) {
    companion object {
        val instance = THandler()
    }

}