package com.app.qqwpick.adapter

import com.app.qqwpick.R
import com.app.qqwpick.data.home.OrderThirdListBean
import com.app.qqwpick.databinding.ItemThirdOrderSendBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class ThirdOrderSendListAdapter(list: MutableList<OrderThirdListBean>) :
    BaseQuickAdapter<OrderThirdListBean, BaseDataBindingHolder<ItemThirdOrderSendBinding>>(
        R.layout.item_third_order_send, list
    ), LoadMoreModule {
    override fun convert(
        holder: BaseDataBindingHolder<ItemThirdOrderSendBinding>,
        item: OrderThirdListBean
    ) {
        val binding = holder.dataBinding
        binding?.item = item
    }
}