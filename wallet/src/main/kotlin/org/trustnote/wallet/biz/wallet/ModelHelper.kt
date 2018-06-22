package org.trustnote.wallet.biz.wallet

import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyAddresses
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.util.Utils

object ModelHelper {

    fun generateNewAddresses(credential: Credential, isChange: Int): List<MyAddresses> {
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
            myAddress.definition = TTTUtils.genDefinitions(addressPubkey)
            //TODO: check above logic from JS code.
            myAddress
        })

        return res

    }

    fun generateNewAddressAndSaveDb(credential: Credential, isChange: Int): List<MyAddresses> {
        val newAddresses = ModelHelper.generateNewAddresses(credential, isChange)
        DbHelper.saveWalletMyAddress(newAddresses)
        return newAddresses
    }

    // 先把助记词MD5  =  mnemonic.md5(),然后取MD5的第一个字符 char = MD5.characters.first ,
    // 再num = String.getAscii(character:char), 最后index = num % hubs.count 得到hub = hubs[index]

    fun computeHubNumberForPairId(mnemonic: String): Int {
        val md5 = AndroidUtils.md5(mnemonic)
        return md5.toCharArray()[0].toInt()
    }


}
