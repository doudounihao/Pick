package com.app.qqwpick.adapter

import android.view.View
import com.app.qqwpick.R
import com.app.qqwpick.data.home.OrderListBean
import com.app.qqwpick.databinding.ItemOrderSendBinding
import com.app.qqwpick.util.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class OrderSendListAdapter(list: MutableList<OrderListBean>) :
    BaseQuickAdapter<OrderListBean, BaseDataBindingHolder<ItemOrderSendBinding>>(
        R.layout.item_order_send, list
    ), LoadMoreModule {
    override fun convert(holder: BaseDataBindingHolder<ItemOrderSendBinding>, item: OrderListBean) {
        val binding = holder.dataBinding
        binding?.item = item
        val timeStr: Int = DateUtils.calculateTime(item.bespokeTimeTo)
        if (timeStr < 120) {
            binding?.deliveryItemOrderListFragmentStatus?.setVisibility(View.VISIBLE)
            binding?.deliveryItemOrderListFragmentStatus?.setData(
                item.bespokeTimeTo,
                null,
                DateUtils.getCurrentTime1()
            )
        } else {
            binding?.deliveryItemOrderListFragmentStatus?.setVisibility(View.GONE)
        }
    }
}