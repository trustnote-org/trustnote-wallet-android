package org.trustnote.wallet.uiframework

import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import org.trustnote.wallet.R
import org.trustnote.wallet.TApplicationComponent
import org.trustnote.wallet.TApp
import org.trustnote.wallet.widget.keyboard.BasicOnKeyboardActionListener
import org.trustnote.wallet.widget.keyboard.CustomKeyboardView

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var mKeyboardView: CustomKeyboardView
    private lateinit var mKeyboard: Keyboard

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies(TApp.graph)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        //        if (Build.VERSION.SDK_INT >= 21) {
        //            window.statusBarColor = ContextCompat.getColor(this, R.color.bg_white)
        //        }

    }

    override fun onResume() {
        super.onResume()
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
        mKeyboard = Keyboard(this, R.xml.qwerty)
        mKeyboardView.keyboard = mKeyboard
        mKeyboardView.setOnKeyboardActionListener(BasicOnKeyboardActionListener(
                this))
    }

    abstract fun injectDependencies(graph: TApplicationComponent)

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

}

//mTargetView.setOnTouchListener(object : View.OnTouchListener {
//
//    override fun onTouch(v: View, event: MotionEvent): Boolean {
//        // Dobbiamo intercettare l'evento onTouch in modo da aprire la
//        // nostra tastiera e prevenire che venga aperta quella di
//        // Android
//        showKeyboardWithAnimation()
//        return true
//    }
//})

//mKeyboardView.setKeyboard(mKeyboard)


//key_selector.xml:
//<?xml version="1.0" encoding="utf-8"?>
//<selector xmlns:android="http://schemas.android.com/apk/res/android">
//<item
//android:state_checkable="true"
//android:state_pressed="false"
//android:state_checked="false"
//android:drawable="@drawable/key_background_checkable" />
//<item
//android:state_checkable="true"
//android:state_pressed="true"
//android:state_checked="false"
//android:drawable="@drawable/key_background_checkable_pressed" />
//<item
//android:state_checkable="true"
//android:state_checked="true"
//android:state_pressed="false"
//android:drawable="@drawable/key_background_checked"/>
//<item
//android:state_checkable="true"
//android:state_checked="true"
//android:state_pressed="true"
//android:drawable="@drawable/key_background_checked_pressed"/>
//<item
//android:state_checkable="false"
//android:state_checked="false"
//android:state_pressed="true"
//android:drawable="@drawable/key_background_pressed"/>
//<item
//android:state_checkable="false"
//android:state_checked="false"
//android:state_pressed="false"
//android:drawable="@drawable/key_background"/>
//
//</selector>
//
//and in the xml file which extends KeyboardView class, put android:keyBackground="@drawable/key_selector" as follows:
//
//<?xml version="1.0" encoding="utf-8"?>
//<com.keyboard.LatinKeyboardView xmlns:android="http://schemas.android.com/apk/res/android"
//android:id="@+id/keyboard"
//android:layout_width="fill_parent"
//android:layout_height="wrap_content"
//android:layout_alignParentBottom="true"
//android:keyBackground="@drawable/key_selector"
//android:keyTextColor="@android:color/white" />