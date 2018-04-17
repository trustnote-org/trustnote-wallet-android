package org.trustnote.wallet

import android.Manifest
import android.content.Intent
import android.os.Bundle
import org.trustnote.wallet.uiframework.BaseActivity
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import kr.co.namee.permissiongen.PermissionGen
import org.trustnote.wallet.debugui.EmptyFragment
import org.trustnote.wallet.settings.MeFragment

//@RuntimePermissions
class MainActivity : BaseActivity() {

    override fun injectDependencies(graph: TApplicationComponent) {

    }

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            changeFragment(item.itemId)
            true
        }

        selectPageByIntent(intent)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        selectPageByIntent(intent)
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
    }

    private fun selectPageByIntent(intent: Intent) {
        val menuId = intent.getIntExtra(MAINACTIVITY_KEY_MENU_ID, 0)
        //TODO: should default to first page.
        bottomNavigationView.selectedItemId = if (menuId == 0) R.id.action_me else menuId
    }

    //@NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun changeFragment(menuItemId: Int) {
        var newFragment: Fragment = EmptyFragment()
        when (menuItemId) {
        //R.id.action_debug -> newFragment = DebugFragment()
            R.id.action_me -> newFragment = MeFragment()
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