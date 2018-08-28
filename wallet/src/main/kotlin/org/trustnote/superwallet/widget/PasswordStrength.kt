package org.trustnote.superwallet.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import org.trustnote.superwallet.R
import org.trustnote.superwallet.TApp

class PasswordStrength constructor(context: Context,
                                   attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var pwd_strength_bar_weak: View
    private var pwd_strength_bar_general: View
    private var pwd_strength_bar_strong: View

    init {
        val view = View.inflate(context, R.layout.l_pwd_strength, null)
        addView(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        pwd_strength_bar_weak = view.findViewById<View>(R.id.pwd_strength_bar_weak)
        pwd_strength_bar_general = view.findViewById<View>(R.id.pwd_strength_bar_general)
        pwd_strength_bar_strong = view.findViewById<View>(R.id.pwd_strength_bar_strong)
    }

    inline fun isMatch(str: String, regex: String):Int{
        return if (str.contains(regex.toRegex())) 1 else 0
    }

    //不少于8位字符，建议混合大小写字母、数字、特殊符号
    //输入同1种符号一直显示弱；输入2、3种符号，且输入只要少于1-5位密码就是弱；6-10位密码就是中；11位以上就是强；
    fun computPwdStrength(pwd: String):PwdStrength {
        if (pwd.isBlank()) {
            updateUI(PwdStrength.NONE)
            return PwdStrength.NONE
        }

        val hasAlphabet = isMatch(pwd,"[a-zA-Z]+")
        val hasDigital = isMatch(pwd,"[0-9]+")
        val hasNonAlphabetNumeric = isMatch(pwd,"[^A-Za-z0-9]+")

        val totalCategories = hasAlphabet + hasDigital + hasNonAlphabetNumeric

        if (totalCategories == 1) {
            updateUI(PwdStrength.WEAK)
            return PwdStrength.WEAK
        }

        if (totalCategories > 1 && pwd.length <= 5) {
            updateUI(PwdStrength.WEAK)
            return PwdStrength.WEAK
        }

        if (totalCategories > 1 && pwd.length <= 10) {
            updateUI(PwdStrength.NORMAL)
            return PwdStrength.NORMAL
        }

        if (totalCategories > 1 && pwd.length > 10) {
            updateUI(PwdStrength.STRONG)
            return PwdStrength.STRONG
        }

        return PwdStrength.NONE
    }

    private fun updateUI(pwdStrength: PwdStrength) {
        when (pwdStrength) {
            PwdStrength.NONE -> {
                pwd_strength_bar_weak.setBackgroundResource(R.color.t_strength_none)
                pwd_strength_bar_general.setBackgroundResource(R.color.t_strength_none)
                pwd_strength_bar_strong.setBackgroundResource(R.color.t_strength_none)
            }
            PwdStrength.WEAK -> {
                pwd_strength_bar_weak.setBackgroundResource(R.color.t_strength_weak)
                pwd_strength_bar_general.setBackgroundResource(R.color.t_strength_none)
                pwd_strength_bar_strong.setBackgroundResource(R.color.t_strength_none)
            }
            PwdStrength.NORMAL -> {
                pwd_strength_bar_weak.setBackgroundResource(R.color.t_strength_weak)
                pwd_strength_bar_general.setBackgroundResource(R.color.t_strength_general)
                pwd_strength_bar_strong.setBackgroundResource(R.color.t_strength_none)
            }
            PwdStrength.STRONG -> {
                pwd_strength_bar_weak.setBackgroundResource(R.color.t_strength_weak)
                pwd_strength_bar_general.setBackgroundResource(R.color.t_strength_general)
                pwd_strength_bar_strong.setBackgroundResource(R.color.t_strength_strong)
            }
        }
    }

}

enum class PwdStrength {
    NONE,
    WEAK,
    NORMAL,
    STRONG
}