package org.trustnote.wallet.settings

import android.webkit.ValueCallback
import org.trustnote.wallet.BuildConfig

import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.pojo.SendPaymentInfo
import org.trustnote.wallet.tttui.QRFragment
import org.trustnote.wallet.biz.units.UnitComposer
import org.trustnote.wallet.biz.wallet.*
import org.trustnote.wallet.js.restoreWallet
import org.trustnote.wallet.util.Utils

import java.util.ArrayList
import java.util.Arrays

object SettingsDataFactory {

    val GROUP_TEST = "Test"
    val GROUP_WALLET = "Wallet management"
    val GROUP_MNEMONIC = "Restore Test"

    fun makeSettings(): List<SettingGroup> {
        if (BuildConfig.FLAVOR == "devnet" && BuildConfig.DEBUG) {
            return Arrays.asList(makeTestGroup(),
                    makeWalletGroup(), makeSeedsGroup())
        } else {
            return Arrays.asList(makeTestGroup(),
                    makeWalletGroup())
        }
    }

    fun makeTestGroup(): SettingGroup {
        return SettingGroup(GROUP_TEST, makeTests())
    }

    fun makeTests(): List<SettingItem> {

        val res = ArrayList<SettingItem>()

        val testPostTx = SettingItem("Test: Test post tx")
        testPostTx.action = Runnable {

            val sendPaymentInfo = SendPaymentInfo("LyzbDDiDedJh+fUHMFAXpWSiIw/Z1Tgve0J1+KOfT3w=", "CDZUOZARLIXSQDSUQEZKM4Z7X6AXTVS4", 17000000L)

            UnitComposer(sendPaymentInfo).startSending()


//            JsTest.testPostTx()
        }
        res.add(testPostTx)

        val testHistory = SettingItem("Test: get_history, check res from log")
        testHistory.action = Runnable { WalletManager.model.hubRequestCurrentWalletTxHistory() }
        res.add(testHistory)


//        val testJSSignWithDeviceMessageHash = SettingItem("testJSSignWithDeviceMessageHash", false)
//        testJSSignWithDeviceMessageHash.action = Runnable {
//            val orignData = "{\"challenge\":\"C4q7l7jDyLVvFB9YDQu2M3J1N0PC/9NkNwbUNQLa\",\"pubkey\":\"A1woEiM/LdDHLvTYUvlTZpsTI+82AphGZAvHalie5Nbw\",\"signature\":\"cphmz+vksrdwMDAUlNNXUPo+1oL7fTaYiKoI0rQNflZpOyQBZpovA3s79HTByxvWrUo2Wy/NerrpsLzaaXm/2g==\"}"
//
//            JSApi().getDeviceMessageHashToSign(orignData, ValueCallback { hashValue ->
//                Utils.debugLog(hashValue)
//
//                //fun sign(b64_hash: String, xPrivKey: String, path: String, cb: ValueCallback<String>) {
//
//                JSApi().sign(hashValue, WalletManager.modelgetProfile()!!.xPrivKey, "\"m/1'\"", ValueCallback { signRes ->
//                    Utils.debugLog(signRes)
//
//                    // /r1gbvHPi8NLGpKoderkk1QJHbooOrDEi81rE2sXKtYCQFDHPxCdvmPPj17czehyptxL3T7dPKK2FqACbcdyiQ==
//                })
//            })
//        }
//        res.add(testJSSignWithDeviceMessageHash)


//        val testJSVerifySign = SettingItem("testJSVerifySign", false)
//        testJSVerifySign.action = Runnable { runTestJSVerifySign() }
//        res.add(testJSVerifySign)


        val testQrCode = SettingItem("Test QR CODE")
        testQrCode.action = Runnable { SimpleFragmentActivity.startMe(QRFragment::class.java.canonicalName) }
        res.add(testQrCode)

        return res
    }

    private fun reqHistoryFromHub() {
        WalletManager.model.hubRequestCurrentWalletTxHistory()
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
            newSeed.action = Runnable { CreateWalletActivity.startMe() }
            res.add(newSeed)
        } else {
            for (credential in WalletManager.model.mProfile.credentials) {
                val oneWallet = SettingItem(credential.toString())
                res.add(oneWallet)
            }

            val newWallet = SettingItem("+  New")
            newWallet.action = Runnable { SimpleFragmentActivity.startMe(SimpleFragment::class.java.canonicalName) }
            res.add(newWallet)
        }

        return res
    }

    fun makeSeedsGroup(): SettingGroup {
        return SettingGroup(GROUP_MNEMONIC, makeSeedsTestCase())
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

}

