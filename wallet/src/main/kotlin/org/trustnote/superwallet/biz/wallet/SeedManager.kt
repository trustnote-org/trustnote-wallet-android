package org.trustnote.superwallet.biz.wallet

import org.trustnote.superwallet.BuildConfig
import org.trustnote.superwallet.util.AndroidUtils
import org.trustnote.superwallet.util.Utils
import java.io.File

object SeedManager {

    val myfile = File(AndroidUtils.getMySdcardDirectory(), "ttt.txt")
    init {
        saveSeedForTest(TestData.mnemonic0)
        saveSeedForTest(TestData.mnemonic1)
        saveSeedForTest(TestData.mnemonic2)
    }

    //Notice: just save seed for dev net and test build.

    fun saveSeedForTest(seed: String) {
        if (!(BuildConfig.FLAVOR == "devnet" && BuildConfig.DEBUG)) {
            return
        }

        val allSeeds = mutableSetOf<String>()
        allSeeds.addAll(getAllSeeds())
        allSeeds.add(seed)
        myfile.bufferedWriter().use { out ->
            allSeeds.forEach {
                out.write("$it\n\r")
            }
        }
    }

    fun getAllSeeds(): List<String> {
        if (!(BuildConfig.FLAVOR == "devnet" && BuildConfig.DEBUG)) {
            return listOf()
        }

        if (!myfile.exists()) {
            return listOf()
        }
        val allSeeds = mutableListOf<String>()
        myfile.forEachLine { if (it.isNotEmpty()) allSeeds.add(it) }
        return allSeeds
    }

}
