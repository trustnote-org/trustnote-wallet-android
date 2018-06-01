package org.trustnote.wallet.biz.wallet

import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyAddresses
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.util.Utils

object ModelHelper {

    private fun generateNewAddresses(credential: Credential, isChange: Int): List<MyAddresses> {
        val api = JSApi()
        val currentMaxAddress = DbHelper.getMaxAddressIndex(credential.walletId, isChange)
        val newAddressSize = if (currentMaxAddress == 0) TTT.walletAddressInitSize else TTT.walletAddressIncSteps
        val res = List(newAddressSize, {

            //Does it really improve UI performance?
            Thread.sleep(50)

            val myAddress = MyAddresses()
            myAddress.account = credential.account
            myAddress.wallet = credential.walletId

            myAddress.isChange = isChange
            myAddress.addressIndex = it + currentMaxAddress
            myAddress.address = Utils.decodeJsStr(api.walletAddressSync(credential.xPubKey, isChange, myAddress.addressIndex))
            val addressPubkey = Utils.decodeJsStr(api.walletAddressPubkeySync(credential.xPubKey, isChange, myAddress.addressIndex))
            myAddress.definition = """["sig",{"pubkey":"$addressPubkey"}]"""
            //TODO: check above logic from JS code.
            myAddress
        })

        return res

    }

    fun generateNewAddressAndSaveDb(credential: Credential) {
        val newAddresses = generateNewAddress(credential)
        DbHelper.saveWalletMyAddress(newAddresses)
    }

    private fun generateNewAddress(newCredential: Credential): List<MyAddresses> {
        val receiveAddresses = ModelHelper.generateNewAddresses(newCredential, TTT.addressReceiveType)
        val changeAddresses = ModelHelper.generateNewAddresses(newCredential, TTT.addressChangeType)

        val res = mutableListOf<MyAddresses>()
        res.addAll(receiveAddresses)
        res.addAll(changeAddresses)
        return res.toList()
    }


}
