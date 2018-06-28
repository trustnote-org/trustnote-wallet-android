package org.trustnote.wallet.network.pojo

import com.google.gson.JsonObject
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.network.HubMsgFactory.CMD_DELIVER

class ReqDeliver : HubRequest {

    constructor(params: JsonObject) : super(CMD_DELIVER, tag = HubModel.instance.getRandomTag()) {
        setReqParams(params)
    }

    fun isAccepted(): Boolean {
        return  "accepted" == getResponse().responseJson?.asString
    }

//    ["request",{"command":"hub/deliver","params":{"encrypted_package":{"encrypted_message":"r+m+SP0tt+iYmfDGkK+fatVdRcn1TJ4kUR/zrnnSzd4uc4nHOeLmhP/tVwbv2w195xAZT1+l7A8V1s6/HjPM/PEsfRsrST6h/elKdZPHRmtCCXAAiwv82LGY8nWVdRW0IyyvcmCEkX7J+j9TLh9Z7nikjm77Y3Ou6vq5lSRjmSrlPnj14t1XCY/iNXGsbliwOrkYy4uHdDjLBYLrmjicBc8dS2SxZlrDcN6wRPiUiKl+zPMtH0VAvcWoj4/KChuAlo8PNf30DBDkITcadscUqw==","iv":"/R4C7YFYT/9g8Ymy","authtag":"CTh56+JffTrj1rKnG+Cdrg==","dh":{"sender_ephemeral_pubkey":"AjNS+oaCrGytUuJBIs/NOswrjfr4ZDSAx/e5QEWNEUAU","recipient_ephemeral_pubkey":"Atc1msG8z4HTTJyT61WKHtL6A4xQLrSmSMaclvZtJdYJ"}},
//
// "to":"0CREQS2362HYCHNKVCU4ZBSNHAVVLASKM","pubkey":"Anx/WXT+huJwRp8zW+nIwKmFBwe1l7X6LtVoIl7bOsW2","signature":"Y28lb2RPRvb2Bgv++3FKPN95eV+Mh/GZSwstva6Trmt8cLfKefikSbCVIytPnnVCL4Iso7HRWPpGzzUUVFNLeQ=="},"tag":"eQlAFU3I6l/edHeudU+WnDb5GZ05KSzBnm+Ao5REuNY="}]

//    ["response",{"tag":"eQlAFU3I6l/edHeudU+WnDb5GZ05KSzBnm+Ao5REuNY=","response":"accepted"}]

//    ["response",{"tag":"RANDOM:-1742712285","response":"accepted"}]
}