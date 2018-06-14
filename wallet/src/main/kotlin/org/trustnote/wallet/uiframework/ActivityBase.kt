package org.trustnote.wallet.uiframework

import android.Manifest
import android.annotation.SuppressLint
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import kr.co.namee.permissiongen.PermissionGen
import org.trustnote.wallet.*
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.keyboard.BasicOnKeyboardActionListener
import org.trustnote.wallet.widget.keyboard.CustomKeyboardView

abstract class ActivityBase : AppCompatActivity() {

    private lateinit var mKeyboardViewWrapper: View
    private lateinit var mKeyboardView: CustomKeyboardView
    private lateinit var mKeyboard: Keyboard

    lateinit var mRefreshingIndicator: View
    lateinit var mErrorIndicator: View

    var stringAsReturnResult: String = ""

    abstract fun injectDependencies(graph: TApplicationComponent)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies(TApp.graph)

        setupStatusBar()
    }

    override fun setContentView(layoutResID: Int) {

        super.setContentView(layoutResID)
        //        mRefreshingIndicator = findViewById(R.id.refreshing_indicator)
        //        mErrorIndicator = findViewById(R.id.error_indicator)
<<<<<<< HEAD

=======
>>>>>>> 4c1ee6ad81d163c66a74efc88f3395afea83b14c
    }

    private fun setupStatusBar() {

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //        if (Build.VERSION.SDK_INT >= 21) {
        //            window.statusBarColor = ContextCompat.getColor(this, R.color.bg_white)
        //        }

    }

    open fun addFragment(f: FragmentBase) {

        addFragment(f, R.id.fragment_container)

    }

    fun addL2Fragment(f: FragmentBase) {

        addFragment(f, R.id.fragment_level2)

    }

    open fun addFragment(f: FragmentBase, fragmentContainerId: Int, isAddToBackStack: Boolean = true) {

        val transaction = supportFragmentManager.beginTransaction()

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

        setupKeyboard()
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
        finish()
        AndroidUtils.startActivity(ActivityStarterChooser::class.java)
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

    //TODO: buggy
    fun setupStringAsReturnResult(s: String) {
        stringAsReturnResult = s
    }

    fun readReturnResultAndClear(): String {
        val res = stringAsReturnResult
        stringAsReturnResult = ""
        return res
    }

}

