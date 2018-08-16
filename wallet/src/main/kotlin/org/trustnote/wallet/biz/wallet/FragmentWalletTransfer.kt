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

class FragmentWalletTransfer : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_wallet_transfer
    }

    lateinit var title: TextView
    lateinit var balance: TMnAmount
    lateinit var receiverText: ClearableEditText
    lateinit var selectAddressIcon: View
    lateinit var receiveErr: TextView

    lateinit var amountText: ClearableEditText
    lateinit var notesText: ClearableEditText
    lateinit var amountErr: TextView
    lateinit var transferAll: TextView
    lateinit var btnConfirm: Button

    override fun initFragment(view: View) {
        super.initFragment(view)

        title = findViewById(R.id.transfer_title)

        balance = findViewById(R.id.transfer_balance)
        balance.setupMiddelFont()

        receiverText = findViewById(R.id.transfer_receiver_hint)
        selectAddressIcon = findViewById(R.id.transfer_receiver_select)
        receiveErr = findViewById(R.id.transfer_receiver_err)
        receiverText.bindingErr = receiveErr

        amountText = findViewById(R.id.transfer_amount)
        amountErr = findViewById(R.id.transfer_receiver_err_overflow)
        amountText.bindingErr = amountErr

        notesText = findViewById(R.id.transfer_notes)


        amountText.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(9, 4))

        transferAll = findViewById(R.id.transfer_transfer_all)
        transferAll.setOnClickListener { setTransferAmount(credential.balance) }

        btnConfirm = findViewById(R.id.transfer_confirm)
        btnConfirm.setOnClickListener { transfer() }

        selectAddressIcon.setOnClickListener {

            val f = FragmentMeAddressesBook()
            AndroidUtils.addFragmentArguments(f, TTT.KEY_WALLET_ID, credential.walletId)
            f.afterSelectLogic = {
                setTransferAddress(it)
            }
            addL2Fragment(f)
        }

        amountText.addTextChangedListener(MyTextWatcher(this))


        receiverText.addTextChangedListener(MyTextWatcher(this))

        if (Utils.isDeveloperFeature()) {
            setTransferAddress("GI6TXYXRSB4JJZJLECF3F5DOTUZ5V7MX")
            setTransferAmount(105000)
        }

        if (arguments != null && arguments.containsKey(TTT.KEY_TRANSFER_QRCODEW)) {
            handleScanRes(arguments.getString(TTT.KEY_TRANSFER_QRCODEW))
        }

        WalletManager.model.prepareForTransfer()

    }

    override fun inflateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.only_scan, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_scan_receive_address -> {
                startScan {
                    handleScanRes(it)
                }
                return true
            }
        }
        return false
    }

    private fun checkInput(): Boolean {
        var res = true
        if (!TTTUtils.isValidAddress(receiverText.text.toString())) {
            receiveErr.visibility = View.VISIBLE
            res = false
        }

        if (!TTTUtils.isValidAmount(amountText.text.toString(), credential.balance)) {
            amountErr.visibility = View.VISIBLE
            res = false
        }
        return res
    }

    private fun transfer() {

        if (!checkInput()) {
            return
        }

//        if (TTTUtils.isValidAddress(receiverText.text.toString())
//                && TTTUtils.isValidAmount(amountText.text.toString(), credential.balance)) {
//            AndroidUtils.enableBtn(btnConfirm)
//        } else {
//            if (amountText.text.isNotEmpty() && !TTTUtils.isValidAmount(amountText.text.toString(), credential.balance)) {
//                amountErr.visibility = View.VISIBLE
//            }
//            AndroidUtils.disableBtn(btnConfirm)
//        }

        var paymentInfo = PaymentInfo()
        paymentInfo.receiverAddress = receiverText.text.toString()
        paymentInfo.amount = TTTUtils.parseTTTAmount(amountText.text.toString())
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

    private fun setTransferAmount(transferAmount: Long) {
        //TODO: how about fee?
        if (transferAmount <= credential.balance) {
            TTTUtils.formatMN(amountText, transferAmount)
            amountErr.visibility = View.INVISIBLE
        } else {
            amountErr.visibility = View.VISIBLE
        }

        if (transferAmount == 0L) {
            amountText.setText("")
        }

        updateUI()
    }

    private fun setTransferAddress(address: String) {
        if (TTTUtils.isValidAddress(address)) {
            receiverText.setText(address)
            receiveErr.visibility = View.INVISIBLE
        } else {
            receiverText.setText(address)
            receiveErr.visibility = View.VISIBLE
        }
        updateUI()
    }

    private fun handleScanRes(scanRes: String) {
        val paymentInfo = TTTUtils.parsePaymentFromQRCode(scanRes)
        setTransferAmount(paymentInfo.amount)
        setTransferAddress(paymentInfo.receiverAddress)
        updateUI()
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

        balance.setMnAmount(credential.balance)
        amountErr.visibility = View.INVISIBLE

        val btnEnabled = (receiverText.text.isNotEmpty() && amountText.text.isNotEmpty())
        AndroidUtils.enableBtn(btnConfirm, btnEnabled)

    }

}

