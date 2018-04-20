package org.trustnote.wallet.network

import org.trustnote.wallet.network.hubapi.HubPackageBase

class ReqTagMapping {

    private val cache = HashMap<String, HubPackageBase.BODY_TYPE>()

    fun put(tag: String, bodyType: HubPackageBase.BODY_TYPE) {
        cache.put(tag, bodyType)
    }

    fun getExpectedResBodyType(tag: String): HubPackageBase.BODY_TYPE {
        return cache.get(tag)!!
    }
}
