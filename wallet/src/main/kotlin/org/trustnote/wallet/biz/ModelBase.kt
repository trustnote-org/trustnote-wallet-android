package org.trustnote.wallet.biz

import org.trustnote.wallet.util.Utils

open class ModelBase {

    var TAG = "ModelBase"
    fun debug(s: String) {
        Utils.debugLog("""$TAG::$s""")
    }

    fun method(s: String, vararg args: Any) {
        Utils.debugLog("""$TAG::$s::${args.joinToString { it.toString() }}""" )
    }

}