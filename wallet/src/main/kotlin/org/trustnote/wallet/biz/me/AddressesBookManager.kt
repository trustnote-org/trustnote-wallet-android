package org.trustnote.wallet.biz.me

import org.trustnote.db.entity.TransferAddresses
import org.trustnote.wallet.biz.wallet.Credential
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.Prefs

class AddressesBookManager {

    companion object {

        @Synchronized
        fun addAddress(address: String, memo: String) {
            if (address.isEmpty() || memo.isEmpty()) {
                return
            }
            val a = TransferAddresses()
            a.address = address
            a.name = memo
            addAddress(a)
        }

        @Synchronized
        fun addAddress(address: TransferAddresses) {
            val db = getDao()
            val newRes = mutableSetOf<TransferAddresses>()
            newRes.add(address)
            newRes.addAll(db.addresses)
            db.addresses = newRes
            Prefs.writeTransferAddresses(db)
        }

        @Synchronized
        fun removeAddress(address: TransferAddresses) {
            val db = getDao()
            val newRes = mutableSetOf<TransferAddresses>()
            newRes.addAll(db.addresses)
            newRes.remove(address)
            db.addresses = newRes
            Prefs.writeTransferAddresses(db)
        }

        @Synchronized
        fun getAddressBook(credential: Credential): List<TransferAddresses> {

            val res = mutableListOf<TransferAddresses>()
            res.addAll(getDao().addresses.toList())

            val myAllWallets = WalletManager.model.getAvaiableWalletsForUser()

            for(one in myAllWallets) {
                when {
                    one.walletId == credential.walletId -> Unit
                    one.myReceiveAddresses.isEmpty() -> Unit
                    else -> {
                        val a = TransferAddresses()
                        a.address = one.myReceiveAddresses[0].address
                        a.name = one.walletName
                        res.add(a)
                    }
                }
            }

            return res
        }



        @Synchronized
        fun getDao(): AddressesBookDb {
            return Prefs.readTransferAddressesDb()
        }
    }

}