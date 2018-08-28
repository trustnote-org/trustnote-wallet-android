package org.trustnote.superwallet.biz.wallet

import android.view.View
import org.trustnote.db.entity.ChatMessages
import org.trustnote.db.entity.Friend
import org.trustnote.superwallet.R
import org.trustnote.superwallet.TApp
import org.trustnote.superwallet.util.Utils
import java.nio.file.Paths

class TestData {
    companion object {

        //const val mnemonic0 = "what fault measure beach horn decorate armor hover raven diamond bone refuse"
        //const val mnemonic0 = "win feed current today toilet firm carry toss coil sentence license canoe"
        //

        //const val mnemonic0 = "output trick motion simple sauce fossil squirrel secret glove panic borrow romance"
        //const val mnemonic0 = "theme wall plunge fluid circle organ gloom expire coach patient neck clip"
        const val mnemonic0 = "what fault measure beach horn decorate armor hover raven diamond bone refuse"
        const val mnemonic1 = "dragon test equip crew file acoustic public myth alley siege vanish luggage"
        const val mnemonic2 = "together knife slab material electric broom wagon heart harvest side copper vote"
        const val mnemonic3 = "upset noble bulb mixture essay clinic regular waste ladder real evil wisdom"

//        const val mnemonic0 = "what fault measure beach horn decorate armor hover raven diamond bone refuse"
//        const val mnemonic1 = "what fault measure beach horn decorate armor hover raven diamond bone refuse"
//        const val mnemonic2 = "what fault measure beach horn decorate armor hover raven diamond bone refuse"
//        const val mnemonic3 = "what fault measure beach horn decorate armor hover raven diamond bone refuse"

        val a = emptyList<String>()
        val c = View.MeasureSpec.AT_MOST
        const val newVersionInfo =
                """
                    {
                            "version": "2.0.1",
                            "ignore": true,
                            "msg": {
                            "en": [
                            "Support scan code to login iToken platform",
                            "Support scan code to make payment in iToken platform",
                            "Optimize Smart Contract"
                            ],
                            "cn": [
                            "新增扫码登录iToken平台",
                            "新增扫码支付iToken平台订单",
                            "优化智能合约相关问题"
                            ]
                            }
                    }
                """

        const val password = "11111111"

        val loremMessags = TApp.getString(R.string.lorem_ttt)

        fun getTestMnemonic(): String {
            val random = Utils.random.nextInt(4)
            when (random) {

                0 -> return mnemonic0
                1 -> return mnemonic1
                2 -> return mnemonic2
                3 -> return mnemonic3

            }
            return mnemonic2
        }

        fun getRandomMessage(maxLength: Int, minLength: Int = 1): String {

            val length = Utils.random.nextInt((maxLength - minLength) / 2) + minLength
            val start = Utils.random.nextInt(loremMessags.length - length)
            val res = loremMessags.substring(start, start + length)

            return if (res.isEmpty()) getRandomMessage(maxLength, minLength) else res

        }

        fun createATestChatMsgs(): ChatMessages {

            val res = ChatMessages()
            res.correspondentAddress = "ZKWUHD6P3CRLTX64TGPIDQ7U4PM7R7AI232342354254252"
            res.correspondentName = TestData.getRandomMessage(8, 3)
            res.creationDate = System.currentTimeMillis() / 1000 - Utils.random.nextInt(2 * 24 * 3600)
            res.message = TestData.getRandomMessage(138)
            res.unReadMsgsNumber = Utils.random.nextInt(10)
            return res

        }

        fun createAFriend(): Friend {

            val res = Friend()
            res.address = "ZKWUHD6P3CRLTX64TGPIDQ7U4PM7R7AI232342354254252"
            res.name = TestData.getRandomMessage(8, 3)
            return res

        }

        fun getHomelistForMsgs(): List<ChatMessages> {

            var testData = mutableListOf<ChatMessages>()
            for (i in 0..60) {
                testData.add(createATestChatMsgs())
            }
            return testData

        }


    }
}


