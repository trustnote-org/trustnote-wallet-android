
package org.trustnote.wallet.widget.keyboard

import android.content.Context
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener

class CustomKeyboardView(context: Context, attrs: AttributeSet) : KeyboardView(context, attrs) {

    fun showWithAnimation(animation: Animation) {
        animation.setAnimationListener(object : AnimationListener {

            override fun onAnimationStart(animation: Animation) {
                // TODO Auto-generated method stub

            }

            override fun onAnimationRepeat(animation: Animation) {
                // TODO Auto-generated method stub

            }

            override fun onAnimationEnd(animation: Animation) {
                visibility = View.VISIBLE
            }
        })

        setAnimation(animation)
    }

}
