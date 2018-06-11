package org.trustnote.wallet.biz.me

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.trustnote.wallet.R
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
            openFragment(FragmentMeAddressesBookAdd())
        }

        AndroidUtils.addItemClickListenerForRecycleView(listView) {

            val a = (listView.adapter as AddressBookAdapter).myDataset[it]
            (activity as ActivityBase).setupStringAsReturnResult(a.address)
            onBackPressed()

        }

    }

    override fun updateUI() {
        super.updateUI()
        listView.adapter = AddressBookAdapter(AddressesBookManager.getAddressBook())
    }
}

