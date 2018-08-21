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
import org.trustnote.wallet.biz.home.FragmentMainCreateWalletNormal
import org.trustnote.wallet.biz.home.FragmentMainWallet
import org.trustnote.wallet.biz.me.FragmentMeMain
import org.trustnote.wallet.biz.me.SettingItem
import org.trustnote.wallet.biz.me.SettingItemsGroup
import org.trustnote.wallet.biz.me.createNewWallet
import org.trustnote.wallet.biz.msgs.FragmentMsgMyPairId
import org.trustnote.wallet.biz.msgs.FragmentMsgsChat
import org.trustnote.wallet.biz.msgs.FragmentMsgsContactsList
import org.trustnote.wallet.biz.msgs.MessageModel
import org.trustnote.wallet.biz.wallet.FragmentWalletUploadText
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.uiframework.EmptyFragment
import org.trustnote.wallet.util.AndroidBug5497Workaround
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils

class ActivityMain : ActivityBase() {

    override fun injectDependencies(graph: TApplicationComponent) {

    }

    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        AndroidUtils.changeIconSizeForBottomNavigation(bottomNavigationView)

        disableShiftMode(bottomNavigationView)
        bottomNavigationView.setItemIconTintList(null)

        WalletManager.model.refreshExistWallet()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            changeFragment(item.itemId)
            true
        }

        changeFragment(R.id.menu_wallet)

        //AndroidBug5497Workaround.assistActivity(this)

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun showPopupmenu(scanLogic: () -> Unit) {
        val f = FragmentPopupmenu()
        f.scanLogic = scanLogic
        addFragment(f, R.id.fragment_popmenu, isUseAnimation = false)
    }

    fun showPopupmenuForMsgChat(currentF: FragmentMsgsChat) {
        val f = FragmentPopupmenu()
        f.currentChatRef = currentF
        f.popLayoutId = R.layout.l_quick_action_chat
        addFragment(f, R.id.fragment_popmenu, isUseAnimation = false)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_create_wallet -> {
                createNewWallet(this)
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

    override fun onDestroy() {
        super.onDestroy()
        TApp.userAlreadyInputPwd = false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        selectPageByIntent(intent)

        listener(MessageModel.instance.mMessagesEventCenter) {
            updateMsgIconInBottomNavigation()
        }

        showUpgradeInfoFromPrefs()

    }

    private fun selectPageByIntent(intent: Intent) {

        val menuId = intent.getIntExtra(MAINACTIVITY_KEY_MENU_ID, 0)
        //TODO: should default to first page.
        if (menuId != 0) {
            bottomNavigationView.selectedItemId = if (menuId == 0) R.id.menu_me else menuId
            intent.removeExtra(MAINACTIVITY_KEY_MENU_ID)
        }

        val isFromLanguageChange = intent.getBooleanExtra(AndroidUtils.KEY_FROM_CHANGE_LANGUAGE, false)
        if (isFromLanguageChange) {
            intent.removeExtra(AndroidUtils.KEY_FROM_CHANGE_LANGUAGE)
            SettingItem.openSubSetting(this, SettingItemsGroup.LANGUAGE, R.string.setting_system)
            SettingItem.selectLanguageUI(this)
            return
        }

        val isFromApiShare = intent.getBooleanExtra(AndroidUtils.KEY_FROM_SHARE_API, false)
        if (isFromApiShare) {
            intent.removeExtra(AndroidUtils.KEY_FROM_SHARE_API)
            val f = FragmentWalletUploadText()
            //TODO: if profile is not ready
            val credential = WalletManager.model.getDefaultWallet()
            if (credential != null) {
                AndroidUtils.addFragmentArguments(f, TTT.KEY_WALLET_ID, credential.walletId)
                val attachText = intent.getStringExtra(AndroidUtils.KEY_SHARE_TEXT)
                AndroidUtils.addFragmentArguments(f, AndroidUtils.KEY_SHARE_TEXT, attachText)
                addL2Fragment(f)
                return
            } else {
                Utils.toastMsg("Cannot find available wallet")
            }
        }
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

    private fun updateMsgIconInBottomNavigation() {
        val menu = bottomNavigationView.menu
        val isUnread = MessageModel.instance.hasUnreadMessage()
        menu.findItem(R.id.menu_msg).setIcon(
                if (isUnread) R.drawable.ic_menu_message_unread
                else R.drawable.ic_menu_message)
    }
}

const val MAINACTIVITY_KEY_MENU_ID = "KEY_MENU_ID"
fun startMainActivityWithMenuId(menuId: Int = 0) {
    val intent = Intent(TApp.context, ActivityMain::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.putExtra(MAINACTIVITY_KEY_MENU_ID, menuId)
    TApp.context.startActivity(intent)
}

fun startMainActivityAfterLanguageChanged() {
    val intent = Intent(TApp.context, ActivityMain::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.putExtra(MAINACTIVITY_KEY_MENU_ID, R.id.menu_wallet)
    intent.putExtra(AndroidUtils.KEY_FROM_CHANGE_LANGUAGE, true)
    TApp.context.startActivity(intent)
}