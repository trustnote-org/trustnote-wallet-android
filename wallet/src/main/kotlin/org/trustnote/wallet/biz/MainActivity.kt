package org.trustnote.wallet.biz

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.Menu
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.TApplicationComponent
import org.trustnote.wallet.biz.home.FragmentMainWallet
import org.trustnote.wallet.biz.home.FragmentMainWalletTxList
import org.trustnote.wallet.debugui.EmptyFragment
import org.trustnote.wallet.settings.FragmentMainMe
import org.trustnote.wallet.uiframework.BaseActivity

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

        mToolbar.overflowIcon = TApp.smallIconQuickAction

        disableShiftMode(bottomNavigationView)

    }

    fun setToolbarTitle(s: String) {
        supportActionBar!!.title = s
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        selectPageByIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_action, menu)
        return true
    }

    fun openLevel2Fragment(position: Int) {

        //mToolbar.visibility = View.GONE
        //bottomNavigationView.visibility = View.GONE

        // Create new fragment and transaction
        val newFragment = FragmentMainWalletTxList()
        val bundle = Bundle()
        bundle.putInt("CREDENTIAL_INDEX", position)
        newFragment.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_level2, newFragment)
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()


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
            R.id.menu_me -> newFragment = FragmentMainMe()
            R.id.menu_wallet -> newFragment = FragmentMainWallet()
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