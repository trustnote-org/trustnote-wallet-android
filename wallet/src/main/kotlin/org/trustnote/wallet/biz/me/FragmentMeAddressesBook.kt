package org.trustnote.wallet.biz.me

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.me.AddressesBookManager.Companion.getAddressBook
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.MyDialogFragment

class FragmentMeAddressesBook : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_address_book
    }

    var afterSelectLogic: (String) -> Unit = {}

    lateinit var listView: RecyclerView
    lateinit var addIcon: View
    override fun initFragment(view: View) {

        super.initFragment(view)

        addIcon = view.findViewById(R.id.ic_add)

        listView = view.findViewById(R.id.list)
        listView.layoutManager = LinearLayoutManager(activity)

        addIcon.setOnClickListener {
            val f = FragmentMeAddressesBookAddOrEdit()
            f.afterSave = {updateUI()}
            addL2Fragment(f)
        }

    }

    override fun updateUI() {
        super.updateUI()

        val dataList = getAddressBook(credential)
        val adapter = AddressBookAdapter(dataList)

        adapter.removeLambda = {

            MyDialogFragment.showDialog2Btns(activity, activity.getString(R.string.mn_address_remove_msg, it.name)) {
                AddressesBookManager.removeAddress(it)
                updateUI()
            }

        }

        adapter.editLambda = {
            val f = FragmentMeAddressesBookAddOrEdit()
            f.isNewAddress = false
            f.afterSave = {updateUI()}
            AndroidUtils.addFragmentArguments(f, AndroidUtils.KEY_BUNDLE_ADDRESS, it.address)
            AndroidUtils.addFragmentArguments(f, AndroidUtils.KEY_BUNDLE_MEMO, it.name)
            addL2Fragment(f)
        }

        adapter.itemClickListener = {_, item ->
            afterSelectLogic.invoke(item.address)
            onBackPressed()
        }

        listView.adapter = adapter

        if (dataList.isNotEmpty()) {
            findViewById<View>(R.id.gap_for_list).visibility = View.VISIBLE
        } else {
            findViewById<View>(R.id.gap_for_list).visibility = View.INVISIBLE
        }

    }
}

