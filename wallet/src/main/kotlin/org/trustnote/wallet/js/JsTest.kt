package org.trustnote.wallet.js

import org.trustnote.wallet.TTT
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.biz.wallet.TestData
import org.trustnote.wallet.biz.wallet.WalletManager
import timber.log.Timber


object JsTest {
    fun createFullWallet() = restoreWallet(seed)
    fun findVanityAddress(target: String) = findVanityAddressBg(target)
    var seed = TestData.mnemonic0
    fun testPostTx() = testPostTxIn()
}

fun testPostTxIn() {
    Thread() {

        val api = JSApi()
        val payload = """{
  "version": "1.0",
  "alt": "1",
  "messages": [
    {
      "app": "payment",
      "payload_location": "inline",
      "payload_hash": "1z7NQS9r5KjdtnxRS/DTSgU3bwXAFc6BJP/QDYws5Do=",
      "payload": {
        "outputs": [
          {
            "address": "CDZUOZARLIXSQDSUQEZKM4Z7X6AXTVS4",
            "amount": 17000000
          },
          {
            "address": "FJDDWP4AJ6I44HSKHPXXIX6RSQMH674G",
            "amount": 21337648
          }
        ],
        "inputs": [
          {
            "unit": "yJWsxthUK3D9AIx+hFqZEdlk0O7y+zkL+hBuSssJA60=",
            "message_index": 0,
            "output_index": 1
          }
        ]
      }
    }
  ],
  "authors": [
    {
      "address": "TTE5JNCIVLRUPGBHFC6TDZXAW5GO6U72",
      "authentifiers": {
        "r": "FsUCgbpuW3+lFPmAvAL/YutNCOIjo5Iv0yV7ay7dQ8MHb3qYCpNk9aEzGYQA5PcaHZvY0mBBib3cn4xCOSlvXw=="
      },
      "definition": [
        "sig",
        {
          "pubkey": "A75zjR9ve2Jx81atNw10wVsxEmJc3Y6QJ2MTLSFcr06D"
        }
      ]
    }
  ],
  "parent_units": [
    "UyVkdidA6xVp0DzRUb9k9cEUO+c2a8TCVDFahMvBJT4="
  ],
  "last_ball": "Lf/YhFAVxQC0tRqu6HGLRSKs96OHIIdzvfq8XPM7Xio=",
  "last_ball_unit": "i8ua65frsm/rjDVRZphUfNbb1QyzZtaIEyONw1M52xQ=",
  "witness_list_unit": "MtzrZeOHHjqVZheuLylf0DX7zhp10nBsQX5e/+cA3PQ=",
  "headers_commission": 391,
  "payload_commission": 197,
  "unit": "RPyW28iP255ylBi5anU03oDzcXPnGEUFoU+ipfP5kTI=",
  "timestamp": 1525240748
}
"""



        val unit2 = """{
  "alt": "1",
  "authenfiers": [
    {
      "address": "TTE5JNCIVLRUPGBHFC6TDZXAW5GO6U72",
      "authentifiers": {
        "r": "0"
      },
      "definition": [
        "sig",
        {
          "pubkey": "A75zjR9ve2Jx81atNw10wVsxEmJc3Y6QJ2MTLSFcr06D"
        }
      ]
    }
  ],

  "timestamp": 1525759443,
  "headers_commission": 391,
  "last_ball": "gNSyfDlVW70DwbEJSzLGNkU4fNY7/JcpqKI74DnYXrg=",
  "last_ball_unit": "aYJO52pUJ9eQI3CvldRTE4bu62yiePCbSUqY0qikA/o=",
  "messages": [
    {
      "app": "payment",
      "payload": {
        "inputs": [
          {
            "message_index": 0,
            "output_index": 1,
            "unit": "yJWsxthUK3D9AIx+hFqZEdlk0O7y+zkL+hBuSssJA60="
          }
        ],
        "outputs": [
          {
            "address": "CDZUOZARLIXSQDSUQEZKM4Z7X6AXTVS4",
            "amount": 17000000
          },
          {
            "address": "FJDDWP4AJ6I44HSKHPXXIX6RSQMH674G",
            "amount": 21337648
          }
        ]
      },
      "payload_hash": "1z7NQS9r5KjdtnxRS/DTSgU3bwXAFc6BJP/QDYws5Do=",
      "payload_location": "inline"
    }
  ],
  "parent_units": [
    "thEzi5jnbxpvuINlGYHjzPxdm+lo2z06u21A0vxw7Xo="
  ],
  "payload_commission": 197,
  "unit": "0",
  "version": "1.0",
  "witness_list_unit": "MtzrZeOHHjqVZheuLylf0DX7zhp10nBsQX5e/+cA3PQ="
}"""


        val res = api.getUnitHashToSignSync(unit2)
        Utils.debugJS(res)

        val path = """"m/44'/0'/0'/1/2""""
        val signRes = api.signSync(""""qnGUSJUBLxvmBK1IswPeQnmEWN2wSi4ACf5lJn6pLzY="""",
                WalletManager.model.mProfile.xPrivKey, path)
        //var path = m/44'/" + coin + "'/" + account + "'/"+is_change+"/"+address_index;

        Utils.debugJS("Sign result::::")
        Utils.debugJS(signRes)

        //Client.sign(b64_hash, xPrivKey, path)


//

//                """{
//                     "outputs":[
//                        {
//                           "address":"CDZUOZARLIXSQDSUQEZKM4Z7X6AXTVS4",
//                           "amount":17000000
//                        },
//                        {
//                           "address":"FJDDWP4AJ6I44HSKHPXXIX6RSQMH674G",
//                           "amount":21337648
//                        }
//                     ],
//                     "inputs":[
//                        {
//                           "unit":"yJWsxthUK3D9AIx+hFqZEdlk0O7y+zkL+hBuSssJA60=",
//                           "message_index":0,
//                           "output_index":1
//                        }
//                     ]
//                  }"""


    }.start()
}


fun restoreWallet(seed: String) {
    Thread {
        createFullWalletInternal(seed)
    }.start()
}

fun findVanityAddressBg(target: String) {
    Thread {
        findVanityAddressInternal(target)
    }.start()
}

fun findVanityAddressInternal(target: String) {
    val api = JSApi()
    var mnemonic = api.mnemonicSync()
    var isFound = false
    while (!isFound) {
        mnemonic = api.mnemonicSync()
        val privKey = api.xPrivKeySync(mnemonic)

        val walletPubKey = api.walletPubKeySync(privKey, 0)

        List(20, {
            val oneAddress = api.walletAddressSync(walletPubKey, TTT.addressReceiveType, it)
            Utils.debugJS(oneAddress)

            isFound = isVanity(target, oneAddress)

            if (isFound) {
                Timber.d(mnemonic)
                Timber.d(oneAddress)
            }
            //val onePubKey = api.walletAddressPubkeySync(walletPubKey, it)
        })
    }

    Utils.debugJS("$mnemonic")
    Utils.debugJS("Done")
}

fun isVanity(target: String, oneAddress: String): Boolean {
    return oneAddress.startsWith("\"" + target)
}

fun createFullWalletInternal(seed: String) {

    WalletManager.initWithMnemonic(seed, false)

    Utils.debugJS("Done")

}