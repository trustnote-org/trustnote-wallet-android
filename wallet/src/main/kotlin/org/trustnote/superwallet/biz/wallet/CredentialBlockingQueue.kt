package org.trustnote.superwallet.biz.wallet

import java.util.concurrent.LinkedBlockingQueue

class CredentialQueue() {

    private val queue: LinkedBlockingQueue<Credential> = LinkedBlockingQueue()
    private var currentCredential: Credential? = null

    val size: Int
    get() = queue.size + (if (currentCredential == null) 0 else 1)

    fun isNotEmpty(): Boolean {
        return queue.isNotEmpty()
    }

    fun clear() {
        queue.clear()
    }

    fun contains(credential: Credential): Boolean {
        return queue.contains(credential) || (currentCredential != null && currentCredential == credential)
    }

    fun put(credential: Credential) {
        queue.put(credential)
    }

    fun currentCredentialFinished() {
        currentCredential = null
    }

    fun take(): Credential {
        currentCredential = null
        currentCredential = queue.take()
        return currentCredential!!
    }

    fun isRefreshing(): Boolean {
        return queue.isNotEmpty() || currentCredential != null
    }

}