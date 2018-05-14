package org.trustnote.wallet.biz.wallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.uiframework.BaseFragment
import org.trustnote.wallet.util.AndroidUtils
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


@SuppressLint("ValidFragment")  //TODO: the fragment cannot re-create from tomb.
open class CreateWalletFragment(layoutId: Int) : BaseFragment() {

    val mLayoutId = layoutId
    var mRootView: View = View(TApp.context)

    //TODO: empty constructor.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(mLayoutId, container, false)
        return mRootView
    }

    private fun getMyActivity(): CreateWalletActivity {
        return getActivity() as CreateWalletActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView: WebView? = view.findViewById<WebView>(R.id.pwd_warning)
        if (webView != null) {
            val data = AndroidUtils.readAssetFile("pwd_warning.html")
            val localData = AndroidUtils.replaceTTTTag(data)
            webView.loadDataWithBaseURL("", localData, "text/html", "UTF-8", "")
        }

        mRootView = view
        initFragment(mRootView!!)
    }

    fun onShowPage() {
        if (mRootView is ViewGroup) {
            initFragment(mRootView!!)
            getMyActivity().adjustUIBySetting(getPageSetting(mLayoutId))
        }
    }

    open fun initFragment(view: View) {

    }

    open fun onBackPressed() {
        getMyActivity().finish()
    }

    fun nextPage() {
        getMyActivity().nextPage()
    }

    fun nextPage(pageLayoutId: Int) {
        getMyActivity().nextPage(pageLayoutId)
    }

}

@SuppressLint("ValidFragment")
class CWFragmentDisclaimer(layoutId: Int) : CreateWalletFragment(layoutId) {
    override fun initFragment(view: View) {
        view.findViewById<View>(R.id.agree).setOnClickListener {
            CreateWalletModel.userAgree()
            nextPage(R.layout.f_new_seed_pwd)
        }
    }
}

@SuppressLint("ValidFragment")
class CWFragmentDeviceName(layoutId: Int) : CreateWalletFragment(layoutId) {

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

        val btnConfirm = view.findViewById<Button>(R.id.mnemonic_devicename_confirm)
        btnConfirm.setOnClickListener {
            CreateWalletModel.saveDeviceName(editDeviceName.text.toString())
            nextPage(R.layout.f_new_seed_pwd)
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

@SuppressLint("ValidFragment")
class CWFragmentPwd(layoutId: Int) : CreateWalletFragment(layoutId) {
    override fun initFragment(view: View) {
//        view.findViewById<View>(R.id.agree).setOnClickListener {
//            CreateWalletModel.userAgree()
//            nextPage(R.layout.f_new_seed_pwd)
//        }
    }

    override fun onBackPressed() {
        nextPage(R.layout.f_new_seed_or_restore)
    }

}

@SuppressLint("ValidFragment")
class CWFragmentNewSeedOrRestore(layoutId: Int) : CreateWalletFragment(layoutId) {
    override fun initFragment(view: View) {
        val pwdExist = CreateWalletModel.readPwdHash().isNotBlank()
        var btnNewSeed = view.findViewById<Button>(R.id.btn_new_seed)
        btnNewSeed.setOnClickListener {
            nextPage(if (pwdExist) R.layout.f_new_seed_backup else R.layout.f_new_seed_pwd)
        }
        var btnRestore = view.findViewById<Button>(R.id.btn_restore_seed)
        btnRestore.setOnClickListener {
            nextPage(if (pwdExist) R.layout.f_new_seed_restore else R.layout.f_new_seed_pwd)
        }
    }


}


//    button.setAlpha(.5f);
//    button.setClickable(false);}
//    button.setTextColorAlpha()
