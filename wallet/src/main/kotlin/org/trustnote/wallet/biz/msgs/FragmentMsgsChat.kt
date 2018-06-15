package org.trustnote.wallet.biz.msgs

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import org.trustnote.db.entity.Friend
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.wallet.TestData
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.MyDialogFragment

class FragmentMsgsChat : FragmentMsgsBase() {


    override fun getLayoutId(): Int {
        return R.layout.f_msg_chat
    }

    lateinit var recyclerView: RecyclerView
    lateinit var title: TextView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var friend: Friend
    lateinit var input: EditText

    override fun initFragment(view: View) {

        isBottomLayerUI = true

        super.initFragment(view)

        title = mRootView.findViewById(R.id.title)

        input = mRootView.findViewById(R.id.input)

        recyclerView = mRootView.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        mSwipeRefreshLayout = mRootView.findViewById(R.id.swiperefresh)

        mSwipeRefreshLayout.setProgressViewOffset(true, -60, 40)
        mSwipeRefreshLayout.setOnRefreshListener {
            model.refreshHomeList()
        }

        val friendId = arguments.getString(AndroidUtils.KEY_FRIEND_ID)
        friend = TestData.createAFriend()

        input.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    input.setText("")
                    handled = true

                    /*隐藏软键盘*/
                    val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0)
                    }
                }
                return handled
            }

        })

    }

    override fun inflateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.msg_chat_action, menu)
    }

    override fun updateUI() {
        super.updateUI()

        val latestMsgs = model.latestHomeList
        val deboundedMsgs = getChatHistoryForDisplay(latestMsgs)

        val a = ChatAdapter(deboundedMsgs)
        recyclerView.adapter = a

        mSwipeRefreshLayout.isRefreshing = model.isRefreshing()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val res = super.onOptionsItemSelected(item)
        if (res) return res

        when (item.itemId) {

            R.id.ic_remove_msg_contact -> {
                MyDialogFragment.showDialog2Btns(activity, TApp.context.getString(R.string.msg_for_remove_contacts, "Eason"), {
                    onBackPressed()
                })
                return true
            }

            R.id.ic_clear_chat_history -> {
                MyDialogFragment.showDialog2Btns(activity, TApp.context.getString(R.string.msg_for_clear_chat_history, "Eason"), {
                })
                return true
            }

        }
        return false
    }

}

