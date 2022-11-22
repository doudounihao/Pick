package com.app.qqwpick.adapter

import com.app.qqwpick.R
import com.app.qqwpick.data.home.OrderListBean
import com.app.qqwpick.databinding.ItemOrderFinishBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class FinishOrderListAdapter(list: MutableList<OrderListBean>) :
    BaseQuickAdapter<OrderListBean, BaseDataBindingHolder<ItemOrderFinishBinding>>(
        R.layout.item_order_finish,
        list
    ), LoadMoreModule {
    override fun convert(
        holder: BaseDataBindingHolder<ItemOrderFinishBinding>,
        item: OrderListBean
    ) {
        val binding = holder.dataBinding
        binding?.item = item
    }

}