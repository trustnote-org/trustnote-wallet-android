package org.trustnote.wallet.biz.wallet

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.Authentifiers
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.me.FragmentDialogAskAuthorToSigner
import org.trustnote.wallet.biz.me.FragmentDialogScanSignResult
import org.trustnote.wallet.biz.me.FragmentMeAddressesBook
import org.trustnote.wallet.biz.units.UnitComposer
import android.text.InputFilter
import android.view.*
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.util.*
import org.trustnote.wallet.widget.*

class FragmentWalletUploadText : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_wallet_upload_text
    }

    lateinit var title: TextView
    lateinit var notesText: ClearableEditText
    lateinit var btnConfirm: Button

    override fun initFragment(view: View) {
        super.initFragment(view)

        title = findViewById(R.id.transfer_title)

        notesText = findViewById(R.id.transfer_notes)

        btnConfirm = findViewById(R.id.transfer_confirm)
        btnConfirm.setOnClickListener { transfer() }

        notesText.addTextChangedListener(MyTextWatcher(this))

        notesText.setText(arguments.getString(AndroidUtils.KEY_SHARE_TEXT, ""))

        if (arguments != null && arguments.containsKey(TTT.KEY_UPLOAD_TEXT)) {
            notesText.setText(arguments.getString(TTT.KEY_UPLOAD_TEXT, ""))
        }

        WalletManager.model.prepareForTransfer()

    }

    override fun inflateMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.only_scan, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_scan_receive_address -> {
                return true
            }
        }
        return false
    }

    private fun transfer() {

        var paymentInfo = PaymentInfo()

        //setTransferAddress("GI6TXYXRSB4JJZJLECF3F5DOTUZ5V7MX")
        //setTransferAmount(105000)

        //paymentInfo.receiverAddress = WalletManager.model.receiveAddress(credential)
        //paymentInfo.amount = credential.balance
        paymentInfo.receiverAddress = "GI6TXYXRSB4JJZJLECF3F5DOTUZ5V7MX"
        paymentInfo.amount = 1
        paymentInfo.walletId = credential.walletId
        paymentInfo.textMessage = notesText.text.toString()

        val unitComposer = UnitComposer(paymentInfo)
        if (unitComposer.isOkToSendTx()) {
            askUserInputPwdForTransfer(unitComposer)
        } else {
            unitComposer.showFail()
        }
    }

    private fun askUserInputPwdForTransfer(unitComposer: UnitComposer) {

        val inputPwdDialog = FragmentDialogInputPwd()
        inputPwdDialog.confirmLogic = {
            sendTxOrShowHahsToSign(unitComposer, it)
        }
        addL2Fragment(inputPwdDialog)


        MyThreadManager.instance.runInBack {
            unitComposer.composeUnits()
        }

    }

    private fun sendTxOrShowHahsToSign(unitComposer: UnitComposer, password: String = "") {
        val unSignedAuthor = unitComposer.getOneUnSignedAuthentifier()
        if (unSignedAuthor != null && credential.isObserveOnly) {
            tryToGetOneAuthorSign(unSignedAuthor, unitComposer)
        } else {
            unitComposer.startSendTx(activity as ActivityMain, password)
            activity.onBackPressed()
        }
    }

    private fun tryToGetOneAuthorSign(unSignedAuthor: Authentifiers, unitComposer: UnitComposer) {

        val myAddresses = DbHelper.queryAddressByAddresdId(unSignedAuthor.address)
        val checkCode = TTTUtils.randomCheckCode()

        val qrStr = TTTUtils.getQrCodeForColdToSign(unitComposer.hashToSign,
                Utils.genBip44Path(myAddresses),
                unitComposer.sendPaymentInfo.receiverAddress,
                unitComposer.sendPaymentInfo.amount,
                checkCode)

        val f = FragmentDialogAskAuthorToSigner {
            val scanRes = FragmentDialogScanSignResult(checkCode) {

                unSignedAuthor.authentifiers.remove("r")
                unSignedAuthor.authentifiers.addProperty("r", it)
                sendTxOrShowHahsToSign(unitComposer)

            }

            AndroidUtils.openDialog(activity, scanRes, false)

        }

        AndroidUtils.addFragmentArguments(f, TTT.KEY_QR_CODE, qrStr)
        AndroidUtils.openDialog(activity, f, false)

    }

    override fun onResume() {
        super.onResume()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun onPause() {
        super.onPause()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun updateUI() {

        val btnEnabled = (notesText.text.isNotEmpty())
        AndroidUtils.enableBtn(btnConfirm, btnEnabled)

    }

}

