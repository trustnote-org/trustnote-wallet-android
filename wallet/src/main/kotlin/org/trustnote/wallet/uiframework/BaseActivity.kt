package org.trustnote.wallet.uiframework

import android.Manifest
import android.annotation.SuppressLint
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.keyboard.BasicOnKeyboardActionListener
import org.trustnote.wallet.widget.keyboard.CustomKeyboardView
import android.databinding.adapters.CompoundButtonBindingAdapter.setChecked
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import kr.co.namee.permissiongen.PermissionGen
import org.trustnote.wallet.*
import java.lang.reflect.AccessibleObject.setAccessible
import java.lang.reflect.Array.setBoolean

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var mKeyboardView: CustomKeyboardView
    private lateinit var mKeyboard: Keyboard

    lateinit var mRefreshingIndicator: View
    lateinit var mErrorIndicator: View

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

    open fun openFragment() {
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

        mKeyboardView = findViewById(R.id.keyboard_view)
        mKeyboard = Keyboard(this, R.xml.mnemonic)
        mKeyboardView.keyboard = mKeyboard
        mKeyboardView.setOnKeyboardActionListener(BasicOnKeyboardActionListener(
                this))
    }

    fun closeKeyboard(): Boolean {
        if (mKeyboardView.visibility == View.VISIBLE) {
            mKeyboardView.visibility = View.GONE
            return true
        }
        return false
    }

    fun showKeyboardWithAnimation() {
        if (mKeyboardView.visibility != View.VISIBLE) {
            //            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
            //            mKeyboardView.showWithAnimation(animation)
            mKeyboardView.visibility = View.VISIBLE
        }
    }

    fun iamDone() {
        finish()
        AndroidUtils.startActivity(StarterActivity::class.java)
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
        } catch (e: IllegalAccessException) {
        }

    }

    fun showRefreshingUI(isShow: Boolean = false) {
        mRefreshingIndicator.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun showErrorUI(isShow: Boolean = false) {
        mErrorIndicator.visibility = if (isShow) View.VISIBLE else View.GONE
    }

}

