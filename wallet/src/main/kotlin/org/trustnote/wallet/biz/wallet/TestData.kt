package org.trustnote.wallet.biz.wallet

import org.trustnote.wallet.util.Utils

class TestData {
    companion object {
        const val mnemonic0 = "theme wall plunge fluid circle organ gloom expire coach patient neck clip"
        const val mnemonic1 = "dragon test equip crew file acoustic public myth alley siege vanish luggage"
        const val mnemonic2 = "together knife slab material electric broom wagon heart harvest side copper vote"
        const val password = "qwer1234"

        fun getTestMnemonic(): String {
            val random = Utils.random.nextInt(3)
            when (random) {
                0 -> return mnemonic0
                1 -> return mnemonic1
                2 -> return mnemonic2
            }
            return mnemonic2
        }

    }
}


