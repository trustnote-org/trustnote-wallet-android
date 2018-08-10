package org.trustnote.wallet.biz.msgs

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.db.entity.CorrespondentDevices
import org.trustnote.db.entity.Friend
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.wallet.TestData
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.uiframework.FragmentEditBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.widget.MyDialogFragment

class FragmentMsgsChat : FragmentMsgsBase() {


    override fun getLayoutId(): Int {
        return R.layout.f_msg_chat
    }

    var icQuickAction: View? = null

    private lateinit var recyclerView: RecyclerView
    lateinit var title: TextView
    //private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var correspondentAddresses: String
    private lateinit var correspondentDevices: CorrespondentDevices
    lateinit var input: EditText

    override fun initFragment(view: View) {

        super.initFragment(view)

        correspondentAddresses = arguments.getString(AndroidUtils.KEY_CORRESPODENT_ADDRESSES)
        correspondentDevices = model.findCorrespondentDevice(correspondentAddresses)!!

        title = mRootView.findViewById(R.id.title)

        input = mRootView.findViewById(R.id.input)

        recyclerView = mRootView.findViewById(R.id.list)
        val llm = LinearLayoutManager(activity)
        llm.stackFromEnd = true
        recyclerView.layoutManager = llm

//        mSwipeRefreshLayout = mRootView.findViewById(R.id.swiperefresh)
//
//        mSwipeRefreshLayout.setProgressViewOffset(true, -60, 40)
//        mSwipeRefreshLayout.setOnRefreshListener {
//
//            //TODO:
//            mSwipeRefreshLayout.isRefreshing = false
//
//        }


        input.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage(input.text.toString())
                    input.setText("")
                    handled = true

                    /*隐藏软键盘*/
                    //                    val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE)
                    //                            as InputMethodManager
                    //                    if (inputMethodManager.isActive()) {
                    //                        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0)
                    //                    }
                }
                return handled
            }

        })

        icQuickAction = mToolbar.findViewById(R.id.ic_quick_action_container)

        if (icQuickAction != null) {
            icQuickAction!!.visibility = View.VISIBLE

            icQuickAction!!.findViewById<ImageView>(R.id.ic_quick_action).visibility = View.GONE
            icQuickAction!!.findViewById<ImageView>(R.id.ic_quick_action_chat).visibility = View.VISIBLE

            icQuickAction!!.setOnClickListener {
                (activity as ActivityMain).showPopupmenuForMsgChat(this)
            }
        }


    }

    private fun sendMessage(messages: String) {
        if (messages.isEmpty()) {
            return
        }

        model.sendTextMessage(messages, correspondentDevices)
    }

    override fun inflateMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.msg_chat_action, menu)
    }

    override fun onResume() {
        model.readAllMessages(correspondentAddresses)
        super.onResume()
    }

    override fun onPause() {
        model.readAllMessages(correspondentAddresses)
        super.onPause()
    }

    override fun updateUI() {
        super.updateUI()

        correspondentAddresses = arguments.getString(AndroidUtils.KEY_CORRESPODENT_ADDRESSES)
        correspondentDevices = model.findCorrespondentDevice(correspondentAddresses)!!

        title.text = correspondentDevices.name

        val latestMsgs = model.queryChatMessages(correspondentAddresses)
        val deboundedMsgs = getChatHistoryForDisplay(latestMsgs)

        (recyclerView.layoutManager as LinearLayoutManager).stackFromEnd = deboundedMsgs.size >= 7

        val a = ChatAdapter(deboundedMsgs)
        recyclerView.adapter = a

        //mSwipeRefreshLayout.isRefreshing = model.isRefreshing()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val res = super.onOptionsItemSelected(item)
        if (res) return res

        when (item.itemId) {

            R.id.ic_edit_contacts_memo -> {
                editFriendMemoName(activity as ActivityBase)
                return true
            }

            R.id.ic_remove_msg_contact -> {
                return true
            }

            R.id.ic_clear_chat_history -> {
                MyDialogFragment.showDialog2Btns(activity,
                        TApp.context.getString(R.string.msg_for_clear_chat_history,
                                correspondentDevices.name)) {
                    model.clearChatHistory(correspondentAddresses)
                }
                return true
            }

        }
        return false
    }

    fun removeChatHistory() {
        MyDialogFragment.showDialog2Btns(activity,
                TApp.context.getString(R.string.msg_for_clear_chat_history,
                        correspondentDevices.name)) {
            model.clearChatHistory(correspondentAddresses)
        }
    }

    fun removeContact() {
        MyDialogFragment.showDialog2Btns(activity,
                TApp.context.getString(R.string.msg_for_remove_contacts,
                        correspondentDevices.name)) {

            model.removeCorrespondentDevice(correspondentDevices)
            onBackPressed()
        }
    }

    fun editFriendMemoName(activity: ActivityBase) {
        val f = FragmentEditBase()
        f.buildPage(correspondentDevices.name,
                activity.getString(R.string.msg_friend_memo),
                {
                    it.length <= 10
                },

                {
                    correspondentDevices.name = it
                    model.updateCorrespondentDeviceName(correspondentDevices)
                    updateUI()
                },
                pageTitle = activity.getString(R.string.title_edit_friend_memo),
                hint = activity.getString(R.string.hint_edit_friend_memo)

        )

        activity.addL2Fragment(f)

    }

}

