package org.trustnote.wallet.util

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory

class MyThreadManager {

    companion object {
        val instance = MyThreadManager()
    }

    private var jsExec = createExec("JSSYNC")
    private var exec = createExec("ANOY", 1)

    fun runInBack(lambda: () -> Unit) {
        exec.execute(lambda)
    }

    fun runJSInNonUIThread(lambda: () -> Unit) {
        jsExec.execute(lambda)
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
