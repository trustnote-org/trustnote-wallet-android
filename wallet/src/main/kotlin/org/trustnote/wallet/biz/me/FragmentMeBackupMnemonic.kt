package org.trustnote.wallet.biz.me

import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.wallet.FragmentWalletBase
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.widget.FragmentDialogInputPwd
import org.trustnote.wallet.widget.MnemonicsGridView
import org.trustnote.wallet.widget.MyDialogFragment

class FragmentMeBackupMnemonic : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_backup_mnemonic
    }

    //TODO: listen the wallet update event.

    lateinit var hideLayout: View
    lateinit var showLayout: View
    lateinit var backupLayout: View
    lateinit var removeBtn: Button
    lateinit var subTitle: TextView
    lateinit var backupWarning: View

    override fun initFragment(view: View) {

        super.initFragment(view)

        subTitle = findViewById(R.id.new_mnemonic_sub_title)
        subTitle.setText(R.string.backup_mnemonic_sub_title)

        hideLayout = findViewById(R.id.hide_mnemonic_layout)
        showLayout = findViewById(R.id.show_mnemonic_layout)

        backupLayout = findViewById(R.id.mnemonic_backup_layout)

        removeBtn = findViewById(R.id.mnemonic_remove_btn)

        backupWarning = findViewById(R.id.backup_warning)

        hideLayout.setOnClickListener {
            hideMnemonic(true)
        }

        showLayout.setOnClickListener {

            val f = FragmentDialogInputPwd()
            f.confirmLogic = {
                hideMnemonic(false)
            }
            addL2Fragment(f)
        }

        removeBtn.setOnClickListener {
            MyDialogFragment.showDialog2Btns(activity, activity.getString(R.string.dialog_remove_mnemonic_ask)) {
                WalletManager.model.removeMnemonicFromProfile()
                removeMeFromBackStack()
                addL2Fragment(FragmentMeBackupMnemonicRemoved())
            }
        }

        val mnemonicsGrid = findViewById<MnemonicsGridView>(R.id.mnemonics)

        mnemonicsGrid.setMnemonic(WalletManager.model.mProfile.mnemonic, false)

        val webView: WebView = view.findViewById(R.id.backup_warning)

        AndroidUtils.setupWarningWebView(webView, R.string.MNEMONIC_BACKUP_WARNING1,
                R.string.MNEMONIC_BACKUP_WARNING2)

        hideMnemonic(true)

    }

    private fun hideMnemonic(isHide: Boolean) {
        backupLayout.visibility = if (isHide) View.INVISIBLE else View.VISIBLE
        hideLayout.visibility = if (isHide) View.INVISIBLE else View.VISIBLE
        showLayout.visibility = if (isHide) View.VISIBLE else View.INVISIBLE
        removeBtn.visibility = if (isHide) View.INVISIBLE else View.VISIBLE
        backupWarning.visibility = if (isHide) View.INVISIBLE else View.VISIBLE
    }
}

