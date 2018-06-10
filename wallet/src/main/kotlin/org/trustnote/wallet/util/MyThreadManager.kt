package org.trustnote.wallet.util

import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

class MyThreadManager {

    companion object {
        val instance = MyThreadManager()
    }

    private val random = Random()
    private var jsNonUIExec = createExec("JSSYNC")
    private var walletModelBg = createExec("WALLETMODEL_BG")
    private var jsSyncInternal = createExec("JSSYNCINTERNAL")
    private var execLowPriority = createExec("LOWPRIORITY", 1)
    private var exec = createExec("ANOY", 1)

    fun runDealyed(delaySeconds: Long, lambda: () -> Unit) {
        execLowPriority.schedule(lambda, delaySeconds, TimeUnit.SECONDS)

    }

    fun newSingleThreadExecutor(threadTag: String): ScheduledExecutorService {
        return createExec(threadTag)
    }

    fun runLowPriorityInBack(lambda: () -> Unit) {
        execLowPriority.execute {
            Thread.sleep((random.nextInt(3) + 3) * 1000L)
            lambda.invoke()
        }
    }

    fun runInBack(lambda: () -> Unit) {
        exec.execute(lambda)
    }

    fun runWalletModelBg(lambda: () -> Unit) {
        walletModelBg.execute(lambda)
    }

    fun runJSInNonUIThread(lambda: () -> Unit) {
        jsNonUIExec.execute(lambda)
    }

    private fun createExec(tag: String, poolSize: Int = 1): ScheduledExecutorService {
        return Executors.newScheduledThreadPool(1, WorkerThreadFactory(tag))
    }

}

class WorkerThreadFactory(val tag: String) : ThreadFactory {
    private var counter: Int = 0

    override fun newThread(r: Runnable?): Thread {
        return Thread(r, """TTT_${tag}_${counter++}""")
    }

}
