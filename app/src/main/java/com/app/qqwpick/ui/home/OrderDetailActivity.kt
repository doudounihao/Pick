package com.app.qqwpick.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qqwpick.R
import com.app.qqwpick.adapter.GoodsAdapter
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.data.home.GoodsBean
import com.app.qqwpick.data.home.OrderListBean
import com.app.qqwpick.databinding.ActivityOrderDetailBinding
import com.hjq.toast.ToastUtils

class OrderDetailActivity : BaseVMActivity<ActivityOrderDetailBinding>() {

    lateinit var mbean: OrderListBean
    lateinit var beanList: MutableList<GoodsBean>

    private val mAdapter by lazy {
        GoodsAdapter(beanList)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mbean = intent.getSerializableExtra("bean") as OrderListBean
        beanList = mbean.orderSplitDetails.toMutableList()
        mBinding.item = mbean
        mBinding.title.tvCenter.text = "订单详情"
        mBinding.title.tvLeft.setOnClickListener {
            finish()
        }

        mBinding.rvGoods.layoutManager = LinearLayoutManager(this)
        mBinding.rvGoods.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        mBinding.tvOrderNoCopy.setOnClickListener {
            val clipboard =
                mContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(
                "simple_text",
                mbean.orderNo
            )
            clipboard.setPrimaryClip(clip)
            ToastUtils.show("已复制")
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_order_detail
    }
}