package org.trustnote.wallet.debug

import android.webkit.ValueCallback
import org.trustnote.wallet.biz.init.ActivityInit

import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.biz.wallet.PaymentInfo
import org.trustnote.wallet.tttui.QRFragment
import org.trustnote.wallet.biz.wallet.*
import org.trustnote.wallet.biz.js.restoreWallet
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils

import java.util.ArrayList
import java.util.Arrays

object SettingsDataFactory {

    val GROUP_TEST = "Test"
    val GROUP_TEST_OPTION = "Test Option"
    val GROUP_WALLET = "Wallet management"
    val GROUP_MNEMONIC = "Restore Test"

    fun makeSettings(): List<SettingGroup> {
        return Arrays.asList(makeTestGroup(),
                makeWalletGroup(), makeSeedsGroup(), makeDebugOptionGroup())
    }

    fun makeTestGroup(): SettingGroup {
        return SettingGroup(GROUP_TEST, makeTests())
    }

    fun makeTests(): List<SettingItem> {

        val res = ArrayList<SettingItem>()

        val testQrCode = SettingItem("Test QR CODE")
        //testQrCode.action = Runnable { SimpleFragmentActivityBase.startMe(QRFragment::class.java.canonicalName) }
        res.add(testQrCode)

        return res
    }

    private fun reqHistoryFromHub() {
//        WalletManager.model.hubRequestCurrentWalletTxHistory()
    }

    fun runTestJSVerifySign() {

        val orignData = "\"32R6yukt9vRAL+FAWSbohdDpQhTcO8LcjRix6uBA0NY=\""
        val sign = "\"cphmz+vksrdwMDAUlNNXUPo+1oL7fTaYiKoI0rQNflZpOyQBZpovA3s79HTByxvWrUo2Wy/NerrpsLzaaXm/2g==\""

        val pubKey = "\"A1woEiM/LdDHLvTYUvlTZpsTI+82AphGZAvHalie5Nbw\""
        JSApi().verify(orignData, sign, pubKey, ValueCallback { signRes -> Utils.debugLog(signRes) })

    }

    fun makeWalletGroup(): SettingGroup {
        return SettingGroup(GROUP_WALLET, makeWallets())
    }

    fun makeWallets(): List<SettingItem> {
        val res = ArrayList<SettingItem>()
        if (!WalletManager.isExist()) {
            val newSeed = SettingItem("create wallet from new seed", true)
            newSeed.action = Runnable { ActivityInit.startMe() }
            res.add(newSeed)
        } else {
            for (credential in WalletManager.model.mProfile.credentials) {
                val oneWallet = SettingItem(credential.toString())
                res.add(oneWallet)
            }

            val newWallet = SettingItem("+  New")
            newWallet.action = Runnable { SimpleFragmentActivityBase.startMe(SimpleFragment::class.java.canonicalName) }
            res.add(newWallet)
        }

        return res
    }

    fun makeSeedsGroup(): SettingGroup {
        return SettingGroup(GROUP_MNEMONIC, makeSeedsTestCase())
    }

    fun makeDebugOptionGroup(): SettingGroup {
        return SettingGroup(GROUP_TEST_OPTION, makeDebugOption())
    }

    fun makeSeedsTestCase(): List<SettingItem> {

        val res = ArrayList<SettingItem>()
        if (WalletManager.isExist()) {
            val newSeed = SettingItem("Save current seed for future test", true)
            newSeed.action = Runnable {
                SeedManager.saveSeedForTest(WalletManager.model.mProfile.mnemonic)
            }
            res.add(newSeed)
        }

        val allSeeds = SeedManager.getAllSeeds()
        allSeeds.forEach {
            val newSeed = SettingItem("Restore seed from: " + it, true)
            newSeed.action = Runnable {
                restoreWallet(it)
            }
            res.add(newSeed)
        }

        return res
    }

    fun makeDebugOption(): List<SettingItem> {

        val res = ArrayList<SettingItem>()

        if (WalletManager.isExist()) {

            val disablePwd = SettingItem("下次启动时，不用输入密码", true)
            disablePwd.action = Runnable {
                Prefs.writeEnablepwdForStartup(false)
            }
            res.add(disablePwd)

            val enablePwd = SettingItem("下次启动时，必须输入密码", true)
            enablePwd.action = Runnable {
                Prefs.writeEnablepwdForStartup(true)
            }
            res.add(enablePwd)

            val exportAppData = SettingItem("导出APP数据到手机SD卡", true)
            exportAppData.action = Runnable {
                AndroidUtils.exportDataForDebug()
            }
            res.add(exportAppData)

        }

        return res
    }
}

