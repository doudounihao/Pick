package com.app.qqwpick.ui.home

import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseVMFragment
import com.app.qqwpick.databinding.FragmentHomeBinding
import com.app.qqwpick.util.STORE_NAME
import com.app.qqwpick.util.SpUtils
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : BaseVMFragment<FragmentHomeBinding>() {

    var type = 1

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    private val mTitles = arrayOf("到家到店", "三方订单")

    private val mFragmentList by lazy {
        val list = mutableListOf<Fragment>()
        list.add(OrderFragment())
        list.add(ThirdFragment())
        list
    }

    override fun initView(view: View) {
        mBinding.tvStoreName.text = SpUtils.getString(STORE_NAME)

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
            tab.text = mTitles[position]
        }.attach()
        mBinding.viewPager2.offscreenPageLimit = 2

        mBinding.tvSearch.setOnClickListener {
            when (type) {
                1 -> {
                    var intent = Intent(context, OrderSearchActivity::class.java)
                    startActivity(intent)
                }
                2 -> {
                    var intent = Intent(context, OrderLoadSearchActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}