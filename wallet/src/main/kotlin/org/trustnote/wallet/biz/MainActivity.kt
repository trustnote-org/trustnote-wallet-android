package org.trustnote.wallet.biz

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.TApplicationComponent
import org.trustnote.wallet.biz.home.FragmentMainCreateWallet
import org.trustnote.wallet.biz.home.FragmentMainWallet
import org.trustnote.wallet.biz.me.FragmentMeMain
import org.trustnote.wallet.biz.msgs.FragmentMsgsContactsAdd
import org.trustnote.wallet.biz.msgs.FragmentMsgsContactsList
import org.trustnote.wallet.debugui.EmptyFragment
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils

class MainActivity : BaseActivity() {

    override fun injectDependencies(graph: TApplicationComponent) {

    }

    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var mToolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)

        mToolbar.overflowIcon = TApp.smallIconQuickAction

        disableShiftMode(bottomNavigationView)

        setupToolbar()

    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (supportFragmentManager.backStackEntryCount == 0) {
            bottomNavigationView.visibility = View.VISIBLE
            setupToolbar()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_create_wallet -> {
                openLevel2Fragment(Bundle(), FragmentMainCreateWallet::class.java)
            }
            R.id.action_my_pair_id -> {
                AndroidUtils.openDialog(this, R.layout.l_dialog_my_pairid_qr)
            }

            R.id.action_contacts_add -> {
                openLevel2Fragment(Bundle(), FragmentMsgsContactsAdd::class.java)
            }


        }
        return true
    }

    private fun setupToolbar() {
        val spa = supportActionBar!!
        spa.setDisplayShowTitleEnabled(true)

        //getMyActivity().getSupportActionBar()!!.closeOptionsMenu()
        //getMyActivity().supportActionBar!!.setHomeAsUpIndicator(TApp.smallIconBackHome)

        spa.setDisplayHomeAsUpEnabled(false)
        spa.setDisplayShowHomeEnabled(false)

        spa.title = AndroidUtils.getString(R.string.wallet_toolbar_title)
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

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        //inflater.inflate(R.menu.your_menu_xml, menu)
//        //super.onCreateOptionsMenu(menu, inflater)
//    }

    fun openLevel2Fragment(bundle: Bundle, clz: Class<out FragmentBase>) {

        val newFragment = clz.newInstance()
        newFragment.arguments = bundle
        openLevel2Fragment(newFragment)

    }

    fun openLevel2Fragment(f: FragmentBase) {

        val transaction = supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_level2, f)
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()

        bottomNavigationView.visibility = View.INVISIBLE

        //findViewById<View>(R.id.fragment_container).visibility = View.INVISIBLE
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