package org.trustnote.wallet.biz.init

import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.biz.wallet.TestData
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.*

abstract class FragmentInit : FragmentBase() {

    var fromInitActivity = true

    //TODO: empty constructor.
    fun getMyActivity(): ActivityInit {
        return activity as ActivityInit
    }

    override fun initFragment(view: View) {

        if (fromInitActivity) {
            isBottomLayerUI = !getPageSetting(getLayoutId()).showBackArrow
        }

        super.initFragment(view)

        val padding = TApp.resources.getDimensionPixelSize(R.dimen.page_margin_26)

        mRootView.setPadding(padding, 0, padding, 0)

    }

    fun nextPage(pageLayoutId: Int) {
        val page = getPageSetting(pageLayoutId)
        addFragment(page.clz.newInstance())
    }


    fun nextPage(pageLayoutId: Int, nextPageLayoutId: Int) {
        val page = getPageSetting(pageLayoutId)
        val f = page.clz.newInstance()
        AndroidUtils.addFragmentArguments(f, AndroidUtils.KEY_TAG_FOR_NEXT_PAGE, nextPageLayoutId.toString())
        addFragment(f)
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

}

class CWFragmentDisclaimer : FragmentInit() {

    init {
        supportSwipeBack = false
    }

    override fun initFragment(view: View) {
        super.initFragment(view)
        view.findViewById<View>(R.id.agree).setOnClickListener {
            CreateWalletModel.userAgree()
            showFragment(CWFragmentDeviceName())
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.f_init_disclaimer
    }
}

class CWFragmentDeviceName : FragmentInit() {

    init {
        supportSwipeBack = false
    }

    override fun getLayoutId(): Int {
        return R.layout.f_init_devicename
    }

    lateinit var err: TextView
    lateinit var btnConfirm: Button
    lateinit var editDeviceName: ClearableEditText

    override fun initFragment(view: View) {
        super.initFragment(view)

        err = view.findViewById(R.id.mnemonic_devicename_err)
        btnConfirm = view.findViewById(R.id.mnemonic_devicename_confirm)
        editDeviceName = view.findViewById(R.id.mnemonic_devicename_edit_text)

        editDeviceName.addTextChangedListener(MyTextWatcher(this))

        editDeviceName.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) err.visibility = View.INVISIBLE }

        editDeviceName.setText(CreateWalletModel.readDeviceName())

        AndroidUtils.hideErrIfHasFocus(editDeviceName, err)

        btnConfirm.setOnClickListener {

            if (isValidInput(editDeviceName.text.toString())) {

                CreateWalletModel.saveDeviceName(editDeviceName.text.toString())
                showFragment(CWFragmentNewSeedOrRestore())

            } else {

                err.visibility = View.VISIBLE

            }
        }

    }

    override fun updateUI() {
        super.updateUI()
        AndroidUtils.enableBtnIfTextViewIsNotEmpty(editDeviceName, btnConfirm)
    }

    private fun isValidInput(s: String): Boolean {
        return s.length <= 20
    }
}

class CWFragmentNewSeedOrRestore : FragmentInit() {

    init {
        supportSwipeBack = false
    }

    override fun getLayoutId(): Int {
        return R.layout.f_init_create_or_restore
    }

    override fun initFragment(view: View) {
        super.initFragment(view)
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
        super.initFragment(view)

        var btnBackupConfirm = view.findViewById<Button>(R.id.backup_confirm)
        btnBackupConfirm.setOnClickListener {

            MyDialogFragment.showDialog2Btns(getMyActivity(), R.string.dialog_backup_mnemonic_ask, {
                nextPage(R.layout.f_init_verify)
            })
        }

        mnemonicsGrid = view.findViewById(R.id.mnemonics)

        mnemonicsGrid.setMnemonic(CreateWalletModel.tmpMnemonic, false)

        val webView: WebView = view.findViewById(R.id.backup_warning)
        AndroidUtils.setupWarningWebView(webView, "MNEMONIC_BACKUP")


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
        super.initFragment(view)

        var btnBackupConfirm = view.findViewById<Button>(R.id.verify_confirmed)
        AndroidUtils.disableBtn(btnBackupConfirm)
        btnBackupConfirm.setOnClickListener {

            if (mnemonicsGrid.isVerifyOk()) {
                nextPage(R.layout.f_init_remove)
                CreateWalletModel.iamDone()
            } else {
                mnemonicsGrid.showErr()
            }
        }

        mnemonicsGrid = view.findViewById(R.id.mnemonics_verify)
        mnemonicsGrid.setCheckMnemonic(CreateWalletModel.tmpMnemonic)

        //BUG: check the cell immediately after init finished. the cell maybe fill with content for test purpose.
        mnemonicsGrid.onCheckResult = {
            AndroidUtils.enableBtn(btnBackupConfirm, it)
        }

        if (Utils.isDeveloperFeature()) {
            mnemonicsGrid.setMnemonic(CreateWalletModel.tmpMnemonic, true)
        }

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
        super.initFragment(view)

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

open class CWFragmentRestore : FragmentInit() {

    lateinit var mnemonicsGrid: MnemonicsGridView

    override fun getLayoutId(): Int {
        return R.layout.f_init_restore
    }

    override fun initFragment(view: View) {

        super.initFragment(view)

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

        if (Utils.isDeveloperFeature()) {
            mnemonicsGrid.setMnemonic(TestData.getTestMnemonic(), false)
        }

        showMnemonicKeyboardIfRequired()

    }

    private fun checkMnemonicAndForward(isRemove: Boolean) {
        val mnemonics = mnemonicsGrid.getUserInputMnemonic()

        JSApi().xPrivKey(mnemonics, ValueCallback {
            if (it.isNotEmpty() && "0" != it) {
                startRestore(isRemove, mnemonics)
            } else {
                mnemonicsGrid.showErr()
            }
        })

    }

    open fun startRestore(isRemove: Boolean, mnemonics: String) {
        if (CreateWalletModel.getPassphraseInRam().isEmpty()) {
            FragmentDialogInputPwd.showMe(activity) {

                Prefs.saveUserInFullRestore(true)
                CreateWalletModel.savePassphraseInRam(it)

                CreateWalletModel.iamDone(mnemonics, isRemove)
                getMyActivity().iamDone()
            }
        } else {
            CreateWalletModel.iamDone(mnemonics, isRemove)
            getMyActivity().iamDone()
        }
    }

    override fun onBackPressed() {

        if (fromInitActivity) {
            nextPage(R.layout.f_init_create_or_restore)
        } else {
            super.onBackPressed()
        }

    }

}
