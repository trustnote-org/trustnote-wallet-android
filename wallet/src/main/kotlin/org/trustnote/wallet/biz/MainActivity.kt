package org.trustnote.wallet.biz

import android.Manifest
import android.content.Intent
import android.os.Bundle
import org.trustnote.wallet.uiframework.BaseActivity
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.Menu
import kr.co.namee.permissiongen.PermissionGen
import org.trustnote.wallet.BuildConfig
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.TApplicationComponent
import org.trustnote.wallet.debugui.EmptyFragment
import org.trustnote.wallet.settings.MeFragment

class MainActivity : BaseActivity() {

    override fun injectDependencies(graph: TApplicationComponent) {

    }

    private lateinit var bottomNavigationView: BottomNavigationView

    lateinit var mToolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)

        mToolbar.overflowIcon = resources.getDrawable(R.drawable.menu_wallet)
        mToolbar.showOverflowMenu()

        disableShiftMode(bottomNavigationView)

        selectPageByIntent(intent)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        selectPageByIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_action, menu)
        return true
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

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            changeFragment(item.itemId)
            true
        }

    }

    private fun selectPageByIntent(intent: Intent) {
        val menuId = intent.getIntExtra(MAINACTIVITY_KEY_MENU_ID, 0)
        //TODO: should default to first page.
        bottomNavigationView.selectedItemId = if (menuId == 0) R.id.menu_me else menuId
    }

    fun changeFragment(menuItemId: Int) {
        var newFragment: Fragment = EmptyFragment()
        when (menuItemId) {
            R.id.menu_me -> newFragment = MeFragment()
        }

        supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, newFragment)
                .commit()
    }

}

const val MAINACTIVITY_KEY_MENU_ID = "KEY_MENU_ID"
fun startMainActivityWithMenuId(menuId: Int = 0) {
    val intent = Intent(TApp.context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.putExtra(MAINACTIVITY_KEY_MENU_ID, menuId)
    TApp.context.startActivity(intent)
}