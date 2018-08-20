package org.trustnote.wallet.uiframework

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.CallSuper
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.kaopiz.kprogresshud.KProgressHUD
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import kr.co.namee.permissiongen.PermissionGen
import org.trustnote.wallet.*
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.FragmentProgressBlocking
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.upgrade.isNewerVersion
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.network.pojo.WalletNewVersion
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.ContextWrapper
import org.trustnote.wallet.widget.MyDialogFragment
import org.trustnote.wallet.widget.keyboard.BasicOnKeyboardActionListener
import org.trustnote.wallet.widget.keyboard.CustomKeyboardView
import java.util.*

abstract class ActivityBase : AppCompatActivity() {

    private lateinit var mKeyboardViewWrapper: View
    private lateinit var mKeyboardView: CustomKeyboardView
    private lateinit var mKeyboard: Keyboard

    lateinit var mRefreshingIndicator: View
    lateinit var mErrorIndicator: View

    protected val disposables: CompositeDisposable = CompositeDisposable()

    abstract fun injectDependencies(graph: TApplicationComponent)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies(TApp.graph)

        setupStatusBar()
    }

    private fun setupStatusBar() {

        if (this !is ActivityMain) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //        if (Build.VERSION.SDK_INT >= 21) {
        //            window.statusBarColor = ContextCompat.getColor(this, R.color.bg_white)
        //        }

    }

    open fun addFragment(f: FragmentBase, isUseAnimation: Boolean = true) {

        addFragment(f, R.id.fragment_container, isUseAnimation = isUseAnimation)

    }

    fun addL2Fragment(f: Fragment, isUseAnimation: Boolean = true) {

        addFragment(f, R.id.fragment_level2)

    }

    open fun addFragment(f: Fragment, fragmentContainerId: Int, isAddToBackStack: Boolean = true, isUseAnimation: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()

        if (isUseAnimation) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
        }

        if (isAddToBackStack) {

            transaction.add(fragmentContainerId, f)
            transaction.addToBackStack(null)

        } else {

            transaction.replace(fragmentContainerId, f)

        }


        transaction.commit()

    }

    open fun showFragment(f: FragmentBase) {
        addFragment(f, R.id.fragment_container, false)
    }

    override fun onBackPressed() {
        //TODO: weired code.
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_level2)
        if (currentFragment != null && currentFragment is FragmentProgressBlocking && !currentFragment.isDone) {
            return
        }

        if (closeKeyboard()) {

        } else {
            super.onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()

        if (BuildConfig.DEBUG) {
            PermissionGen.with(this)
                    .addRequestCode(100)
                    .permissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    .request()
        }

        listener(WalletManager.mUpgradeEventCenter) {
            handleUpgradeEvent()
        }

        setupKeyboard()

    }

    private fun handleUpgradeEvent() {
        val walletNewVersion = Prefs.readUpgradeInfo()
        if (walletNewVersion != null && !walletNewVersion.ignore) {
            handleUpgradeEvent(walletNewVersion)
        }
    }

    private fun handleUpgradeEvent(walletNewVersion: WalletNewVersion) {
        if (isNewerVersion(walletNewVersion.version)) {

            val title = getString(R.string.upgrade_title)

            val msgList = mutableListOf<String>()
            //msgList.add(title)
            msgList.addAll(walletNewVersion.getUpgradeItems(this))
            val msg = msgList.joinToString("\n")

            if (!walletNewVersion.ignore) {
                MyDialogFragment.showDialog1Btn(this, msg, false, isTextAlignLeft = true, forUpgradeInfoUI = true) {
                    AndroidUtils.openSystemBrowser(TTT.TTT_UPGRADE_WEB_SITE)
                }
            } else {
                MyDialogFragment.showDialog2Btns(this, msg, isTextAlignLeft = true, forUpgradeInfoUI = true) {
                    AndroidUtils.openSystemBrowser(TTT.TTT_UPGRADE_WEB_SITE)
                }
            }
        }
    }

    fun showUpgradeInfoFromPrefs() {

        if (TApp.isAlreadyShowUpgradeInfo) {
            return
        }

        val walletNewVersion = Prefs.readUpgradeInfo()

        if (walletNewVersion != null && isNewerVersion(walletNewVersion.version)) {

            //MyDialogFragment.showMsg(this, R.string.upgrade_already_latest_version)
            handleUpgradeEvent(walletNewVersion)
            TApp.isAlreadyShowUpgradeInfo = true
        }
    }

    fun checkUpgradeInfoFromPrefs() {

        val walletNewVersion = Prefs.readUpgradeInfo()

        if (walletNewVersion != null && isNewerVersion(walletNewVersion.version)) {

            //MyDialogFragment.showMsg(this, R.string.upgrade_already_latest_version)
            handleUpgradeEvent(walletNewVersion)

        } else {

            MyDialogFragment.showMsg(this, R.string.upgrade_already_latest_version)

        }
    }

    private fun isKeyboardExist(): Boolean {
        val keyboardView: View? = findViewById(R.id.keyboard_view)
        return keyboardView != null
    }

    private fun setupKeyboard() {
        if (!isKeyboardExist()) {
            return
        }

        mKeyboardViewWrapper = findViewById(R.id.keyboard_view_wrapper)
        mKeyboardView = findViewById(R.id.keyboard_view)
        mKeyboard = Keyboard(this, R.xml.mnemonic)
        mKeyboardView.keyboard = mKeyboard
        mKeyboardView.setOnKeyboardActionListener(BasicOnKeyboardActionListener(
                this))
    }

    fun closeKeyboard(): Boolean {

        if (!isKeyboardExist()) {
            return false
        }

        if (mKeyboardViewWrapper.visibility == View.VISIBLE) {
            mKeyboardViewWrapper.visibility = View.INVISIBLE
            return true
        }

        return false
    }

    fun showKeyboardWithAnimation() {
        if (mKeyboardViewWrapper.visibility != View.VISIBLE) {
            //            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
            //            AndroidUtils.showWithAnimation(mKeyboardViewWrapper, animation)
            mKeyboardViewWrapper.visibility = View.VISIBLE
        }
    }

    fun hideKeyboardWithAnimation() {
        mKeyboardViewWrapper.visibility = View.INVISIBLE
        //        if (mKeyboardViewWrapper.visibility != View.GONE) {
        //            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom)
        //            AndroidUtils.hidewWithAnimation(mKeyboardViewWrapper, animation)
        //        }
    }

    fun iamDone() {
        AndroidUtils.startActivity(ActivityStarterChooser::class.java)
        finish()
    }

    @SuppressLint("RestrictedApi")
    fun disableShiftMode(view: BottomNavigationView) {
        val menuView = view.getChildAt(0) as BottomNavigationMenuView
        try {
            val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
            shiftingMode.isAccessible = true
            shiftingMode.setBoolean(menuView, false)
            shiftingMode.isAccessible = false
            for (i in 0 until menuView.childCount) {
                val item = menuView.getChildAt(i) as BottomNavigationItemView

                item.setShiftingMode(false)
                // set once again checked value, so view will be updated

                item.setChecked(item.itemData.isChecked)
            }
        } catch (e: NoSuchFieldException) {
            Utils.logW(e.toString())
        } catch (e: IllegalAccessException) {
            Utils.logW(e.toString())
        }

    }

    fun showRefreshingUI(isShow: Boolean = false) {
        mRefreshingIndicator.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun showErrorUI(isShow: Boolean = false) {
        mErrorIndicator.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    fun listener(eventCenter: Subject<Boolean>, function: () -> Unit = { }) {
        val d = eventCenter.observeOn(AndroidSchedulers.mainThread()).subscribe {
            function.invoke()
        }
        disposables.add(d)
    }

    override fun attachBaseContext(newBase: Context) {

        val language = Prefs.readDefaultLanguage()
        var newLocale = Locale.getDefault()
        if (language.isNotEmpty()) {
            newLocale = Locale(language)
        }

        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            val view = getCurrentFocus()
            if (view != null && isShouldHideKeyBord(view, ev)) {
                hideSoftInput(view.getWindowToken())
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private fun hideSoftInput(windowToken: IBinder) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }

    }

    /**
     * 判定当前是否需要隐藏
     */
    fun isShouldHideKeyBord(v: View, ev: MotionEvent): Boolean {
        if (v != null && (v is EditText)) {
            val l = IntArray(2)
            v.getLocationInWindow(l);
            val left = l[0];
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
        }
        return false
    }

    var blockingProgressDialog: KProgressHUD? = null

    fun showBlockProgressDialog() {

        blockingProgressDialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(1)
                .setDimAmount(0.2f)
                .show()
    }

    fun dismissBlockProgressDialog() {
        blockingProgressDialog?.dismiss()
    }

}

