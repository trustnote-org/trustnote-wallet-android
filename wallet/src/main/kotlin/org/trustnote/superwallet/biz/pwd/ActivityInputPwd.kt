package org.trustnote.superwallet.biz.pwd

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.WindowManager
import org.trustnote.superwallet.*
import org.trustnote.superwallet.uiframework.ActivityBase
import org.trustnote.superwallet.util.AndroidUtils


class ActivityInputPwd : ActivityBase() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_input_pwd)

        val f = FragmentInputPwd()
        addFragment(f, isUseAnimation = false)

    }

}