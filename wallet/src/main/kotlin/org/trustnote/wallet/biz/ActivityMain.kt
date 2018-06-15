package org.trustnote.wallet.biz

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.TApplicationComponent
import org.trustnote.wallet.biz.home.FragmentMainCreateWallet
import org.trustnote.wallet.biz.home.FragmentMainWallet
import org.trustnote.wallet.biz.me.FragmentMeMain
import org.trustnote.wallet.biz.msgs.FragmentMsgMyPairId
import org.trustnote.wallet.biz.msgs.FragmentMsgsContactsList
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.EmptyFragment
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils

class ActivityMain : ActivityBase() {

    override fun injectDependencies(graph: TApplicationComponent) {

    }

    lateinit var bottomNavigationView: BottomNavigationView

    var receiveAmount = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        disableShiftMode(bottomNavigationView)
        bottomNavigationView.setItemIconTintList(null)

        WalletManager.model.refreshExistWallet()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_create_wallet -> {
                addL2Fragment(FragmentMainCreateWallet())
                return true
            }
            R.id.action_my_pair_id -> {
                addL2Fragment(FragmentMsgMyPairId())
                return true
            }

        }
        return false
    }

    fun setToolbarTitle(s: String) {
        supportActionBar!!.title = s
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        selectPageByIntent(intent)
    }

    override fun onResume() {
        super.onResume()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            changeFragment(item.itemId)
            true
        }

        selectPageByIntent(intent)

    }

    private fun selectPageByIntent(intent: Intent) {
        val menuId = intent.getIntExtra(MAINACTIVITY_KEY_MENU_ID, 0)
        //TODO: should default to first page.
        bottomNavigationView.selectedItemId = if (menuId == 0) R.id.menu_me else menuId
    }

    fun changeFragment(menuItemId: Int) {
        //TODO: can we do cache?
        var newFragment: Fragment = EmptyFragment()
        when (menuItemId) {
            R.id.menu_me -> newFragment = FragmentMeMain()
            R.id.menu_wallet -> newFragment = FragmentMainWallet()
            R.id.menu_msg -> newFragment = FragmentMsgsContactsList()
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, newFragment)
        transaction.commit()

    }
}

const val MAINACTIVITY_KEY_MENU_ID = "KEY_MENU_ID"
fun startMainActivityWithMenuId(menuId: Int = 0) {
    val intent = Intent(TApp.context, ActivityMain::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.putExtra(MAINACTIVITY_KEY_MENU_ID, menuId)
    TApp.context.startActivity(intent)
}