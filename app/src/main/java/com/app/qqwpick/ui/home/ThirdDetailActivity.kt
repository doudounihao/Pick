package com.app.qqwpick.ui.home

import android.content.ClipData
import android.content.ClipboardManager
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
import com.hjq.toast.ToastUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThirdDetailActivity : BaseVMActivity<ActivityThirdDetailBinding>() {

    var orderNo: String = ""
    private val viewModel: OrdeSendViewModel by viewModels()

    private val beanList by lazy {
        mutableListOf<ThirdGoodsBean>()
    }

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
                    beanList.addAll(it.data?.orderDetails as MutableList<ThirdGoodsBean>)
                    mAdapter.notifyDataSetChanged()

                    mBinding.tvOrderNoCopy.setOnClickListener {
                        val clipboard =
                            mContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            "simple_text",
                            mBinding.item?.channelOrderNo
                        )
                        clipboard.setPrimaryClip(clip)
                        ToastUtils.show("已复制")
                    }

                    mBinding.tvThirdOrderNoCopy.setOnClickListener {
                        val clipboard =
                            mContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            "simple_text",
                            mBinding.item?.orderNo
                        )
                        clipboard.setPrimaryClip(clip)
                        ToastUtils.show("已复制")
                    }

                }
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_third_detail
    }
}