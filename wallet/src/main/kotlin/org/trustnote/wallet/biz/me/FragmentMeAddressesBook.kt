package org.trustnote.wallet.biz.me

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils

class FragmentMeAddressesBook : FragmentBase() {
    override fun getLayoutId(): Int {
        return R.layout.f_me_address_book
    }

    lateinit var listView: RecyclerView
    lateinit var addIcon: View
    override fun initFragment(view: View) {

        super.initFragment(view)

        addIcon = view.findViewById(R.id.ic_add)

        listView = view.findViewById(R.id.list)
        listView.layoutManager = LinearLayoutManager(activity)

        addIcon.setOnClickListener {
            openFragment(FragmentMeAddressesBookAddOrEdit())
        }

    }

    override fun updateUI() {
        super.updateUI()
        val adapter = AddressBookAdapter(AddressesBookManager.getAddressBook())

        adapter.removeLambda = {
            AddressesBookManager.removeAddress(it)
            updateUI()
        }

        adapter.editLambda = {
            val f = FragmentMeAddressesBookAddOrEdit()
            AndroidUtils.addFragmentArguments(f, AndroidUtils.KEY_BUNDLE_ADDRESS, it.address)
            AndroidUtils.addFragmentArguments(f, AndroidUtils.KEY_BUNDLE_MEMO, it.name)
            openFragment(f)
        }

        adapter.itemClickListener = {
            (activity as ActivityBase).setupStringAsReturnResult(it.address)
            onBackPressed()
        }

        listView.adapter = adapter

    }
}

