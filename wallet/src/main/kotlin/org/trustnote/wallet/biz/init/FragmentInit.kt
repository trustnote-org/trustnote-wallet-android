package org.trustnote.wallet.biz.init

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.wallet.TestData
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.MnemonicsGridView
import org.trustnote.wallet.widget.MyDialogFragment


abstract class FragmentInit : FragmentBase() {

    var mNextLayoutId = 0

    //TODO: empty constructor.
    fun getMyActivity(): CreateWalletActivity {
        return activity as CreateWalletActivity
    }

    fun onShowPage() {
        if (mRootView is ViewGroup) {
            initFragment(mRootView!!)
            getMyActivity().adjustUIBySetting(getPageSetting(getLayoutId()))
        }

    }

    fun showMnemonicKeyboardIfRequired() {
        if (isCreated) {
            mRootView.postDelayed({
                mRootView.findViewById<View>(R.id.mnemonic_0)?.requestFocus()
                getMyActivity().showKeyboardWithAnimation()
            }, 150)
        }
    }


    open fun onBackPressed() {
        getMyActivity().finish()
    }

    fun nextPage() {
        if (mNextLayoutId != 0) {
            getMyActivity().nextPage(mNextLayoutId)
        }
    }

    fun nextPage(pageLayoutId: Int) {
        getMyActivity().nextPage(pageLayoutId)
    }

    fun nextPage(pageLayoutId: Int, nexLayoutId: Int) {
        getMyActivity().nextPage(pageLayoutId, nexLayoutId)
    }

}

class CWFragmentDisclaimer : FragmentInit() {
    override fun initFragment(view: View) {
        view.findViewById<View>(R.id.agree).setOnClickListener {
            CreateWalletModel.userAgree()
            nextPage(R.layout.f_init_devicename)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.f_init_disclaimer
    }
}

class CWFragmentDeviceName : FragmentInit() {

    override fun getLayoutId(): Int {
        return R.layout.f_init_devicename
    }

    override fun initFragment(view: View) {
        var editDeviceName = view.findViewById<EditText>(R.id.mnemonic_devicename_edit_text)
        editDeviceName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                afterDeviceNameChanged(s.toString())
            }
        })

        editDeviceName.setText(CreateWalletModel.readDeviceName())
        val btnConfirm = view.findViewById<Button>(R.id.mnemonic_devicename_confirm)
        btnConfirm.setOnClickListener {
            CreateWalletModel.saveDeviceName(editDeviceName.text.toString())
            nextPage(R.layout.f_init_create_or_restore)
        }


        afterDeviceNameChanged(editDeviceName.text.toString())
    }

    fun afterDeviceNameChanged(s: String) {
        val btnConfirm = mRootView.findViewById<Button>(R.id.mnemonic_devicename_confirm)

        if (s.trim().isBlank()) {
            AndroidUtils.disableBtn(btnConfirm)
        } else {
            AndroidUtils.enableBtn(btnConfirm)
        }
    }
}

class CWFragmentNewSeedOrRestore : FragmentInit() {

    override fun getLayoutId(): Int {
        return R.layout.f_init_create_or_restore
    }

    override fun initFragment(view: View) {
        val pwdExist = CreateWalletModel.readPwdHash().isNotBlank()
        var btnNewSeed = view.findViewById<Button>(R.id.btn_new_seed)
        btnNewSeed.setOnClickListener {
            if (pwdExist) {
                nextPage(R.layout.f_init_backup)
            } else {
                nextPage(R.layout.f_init_pwd, R.layout.f_init_backup)
            }
        }
        var btnRestore = view.findViewById<Button>(R.id.btn_restore_seed)
        btnRestore.setOnClickListener {
            if (pwdExist) {
                nextPage(R.layout.f_init_restore)
            } else {
                nextPage(R.layout.f_init_pwd, R.layout.f_init_restore)
            }
        }

        generateTmpMnemonic()

    }

    fun generateTmpMnemonic() {
        JSApi().mnemonic(ValueCallback {
            CreateWalletModel.tmpMnemonic = it
        })
    }

}

class CWFragmentBackup : FragmentInit() {

    override fun getLayoutId(): Int {
        return R.layout.f_init_backup
    }

    lateinit var mnemonicsGrid: MnemonicsGridView
    override fun initFragment(view: View) {

        var btnBackupConfirm = view.findViewById<Button>(R.id.backup_confirm)
        btnBackupConfirm.setOnClickListener {

            MyDialogFragment.showDialog2Btns(getMyActivity(), R.string.dialog_backup_mnemonic_ask, {
                nextPage(R.layout.f_init_verify)
            })
        }

        mnemonicsGrid = view.findViewById(R.id.mnemonics)

        mnemonicsGrid.setMnemonic(CreateWalletModel.tmpMnemonic, false)

        val webView: WebView = view.findViewById(R.id.pwd_warning)
        val data = AndroidUtils.readAssetFile("pwd_warning.html")
        val localData = AndroidUtils.replaceTTTTag(data)
        webView.loadDataWithBaseURL("", localData, "text/html", "UTF-8", "")

        if (isCreated) {
            MyDialogFragment.showMsg(getMyActivity(), R.string.dialog_backup_mnemonic_copy)
        }
    }

    override fun onBackPressed() {
        nextPage(R.layout.f_init_create_or_restore)
    }
}

class CWFragmentVerify : FragmentInit() {

    override fun getLayoutId(): Int {
        return R.layout.f_init_verify
    }

    lateinit var mnemonicsGrid: MnemonicsGridView
    override fun initFragment(view: View) {

        var btnBackupConfirm = view.findViewById<Button>(R.id.verify_confirmed)
        AndroidUtils.disableBtn(btnBackupConfirm)
        btnBackupConfirm.setOnClickListener {
            nextPage(R.layout.f_init_remove)
            CreateWalletModel.iamDone()
        }

        mnemonicsGrid = view.findViewById(R.id.mnemonics_verify)
        mnemonicsGrid.setCheckMnemonic(CreateWalletModel.tmpMnemonic)
        mnemonicsGrid.onCheckResult = {
            AndroidUtils.enableBtn(btnBackupConfirm, it)
        }

//                if (Utils.isUseTestData()) {
//                    mnemonicsGrid.setMnemonic(CreateWalletModel.tmpMnemonic, true)
//                }

        showMnemonicKeyboardIfRequired()

    }

    override fun onBackPressed() {
        nextPage(R.layout.f_init_backup)
    }

}

class CWFragmentRemove : FragmentInit() {

    override fun getLayoutId(): Int {
        return R.layout.f_init_remove
    }

    override fun initFragment(view: View) {

        var btnRemove = view.findViewById<Button>(R.id.mnemonic_remove)
        var btnRemoveIgnore = view.findViewById<Button>(R.id.mnemonic_remove_ignore)

        btnRemove.setOnClickListener {

            MyDialogFragment.showDialog2Btns(activity, R.string.dialog_remove_mnemonic_ask, {
                WalletManager.model.removeMnemonicFromProfile()
                getMyActivity().iamDone()
            })

        }

        btnRemoveIgnore.setOnClickListener {
            getMyActivity().iamDone()
        }
    }

    override fun onBackPressed() {
        //Do nothing.
    }

}


class CWFragmentRestore : FragmentInit() {

    lateinit var mnemonicsGrid: MnemonicsGridView

    override fun getLayoutId(): Int {
        return R.layout.f_init_restore
    }

    override fun initFragment(view: View) {

        mnemonicsGrid = view.findViewById(R.id.mnemonics_restore)
        var btnRestore = view.findViewById<Button>(R.id.mnemonic_restore_btn)
        var btnRestoreRemove = view.findViewById<Button>(R.id.mnemonic_restore_remove_btn)

        btnRestore.setOnClickListener {
            checkMnemonicAndForward(false)
        }

        btnRestoreRemove.setOnClickListener {
            checkMnemonicAndForward(true)
        }

        mnemonicsGrid.onCheckResult = {
            AndroidUtils.enableBtn(btnRestore, it)
            AndroidUtils.enableBtn(btnRestoreRemove, it)
        }

        if (Utils.isUseTestData()) {
            mnemonicsGrid.setMnemonic(TestData.mnemonic2, true)
        }

        showMnemonicKeyboardIfRequired()

    }

    private fun checkMnemonicAndForward(isRemove: Boolean) {
        val mnemonics = mnemonicsGrid.getUserInputMnemonic()
        JSApi().isMnemonicValid(mnemonics) {
            if (it) {
                CreateWalletModel.iamDone(mnemonics, isRemove)
                getMyActivity().iamDone()
            } else {
                mnemonicsGrid.showErr()
            }
        }
    }

    override fun onBackPressed() {
        nextPage(R.layout.f_init_create_or_restore)
    }

}
