package com.app.qqwpick.ui.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qqwpick.R
import com.app.qqwpick.adapter.ThirdGoodsAdapter
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.data.home.ThirdGoodsBean
import com.app.qqwpick.databinding.ActivityThirdDetailBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.viewmodels.OrdeSendViewModel

class ThirdDetailActivity : BaseVMActivity<ActivityThirdDetailBinding>() {

    var orderNo: String = ""
    lateinit var beanList: MutableList<ThirdGoodsBean>
    private val viewModel: OrdeSendViewModel by viewModels()

    private val mAdapter by lazy {
        ThirdGoodsAdapter(beanList)
    }

    override fun initView(savedInstanceState: Bundle?) {
        orderNo = intent.getStringExtra("orderNo").toString()
        mBinding.title.tvCenter.text = "订单详情"
        mBinding.title.tvLeft.setOnClickListener {
            finish()
        }

        mBinding.rvGoods.layoutManager = LinearLayoutManager(this)
        mBinding.rvGoods.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    override fun initData() {
        super.initData()
        viewModel.getThirdDetail(orderNo)
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.thirdDetailBean.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_LOADING ->
                    showLoading()
                DataStatus.STATE_ERROR -> {
                    dismissLoading()
                    toast(it.exception!!.msg)
                }
                DataStatus.STATE_SUCCESS -> {
                    dismissLoading()
                    mBinding.item = it.data
                    beanList = it.data?.orderPayDetails as MutableList<ThirdGoodsBean>
                    mAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_third_detail
    }
}