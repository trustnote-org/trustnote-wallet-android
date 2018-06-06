
package org.trustnote.wallet.widget.keyboard

import android.content.Context
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet

class CustomKeyboardView(context: Context, attrs: AttributeSet) : KeyboardView(context, attrs) {



}

//TODO: UI design will support below resource soon.
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
