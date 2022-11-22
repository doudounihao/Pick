package com.app.qqwpick.ui.home

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseVMFragment
import com.app.qqwpick.databinding.FragmentOrderBinding
import com.app.qqwpick.databinding.FragmentThirdBinding
import com.google.android.material.tabs.TabLayoutMediator

class ThirdFragment : BaseVMFragment<FragmentThirdBinding>() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_third
    }

    private val mTitles = arrayOf("配送中订单")

    private val mFragmentList by lazy {
        val list = mutableListOf<Fragment>()
        list.add(ThirdOrderSendFragment())
//        list.add(OrderLoadFragment())
        list
    }

    override fun initView(view: View) {
        mBinding.viewPager2.adapter =
            object : FragmentStateAdapter(childFragmentManager, lifecycle) {
                override fun getItemCount(): Int {
                    return mFragmentList.size
                }

                override fun createFragment(position: Int): Fragment {
                    return mFragmentList[position]
                }

            }
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager2) { tab, position ->
            tab.setCustomView(R.layout.layout_item)
            var tabText = tab.customView?.findViewById<TextView>(R.id.textview)
            if (tabText != null) {
                tabText.text = mTitles[position]
            }
        }.attach()
        mBinding.viewPager2.offscreenPageLimit = 1
    }

    fun setType(type: Int) {
        (parentFragment as HomeFragment).type = type
    }

}