package com.app.qqwpick.adapter

import com.app.qqwpick.R
import com.app.qqwpick.data.home.OrderLoadListBean
import com.app.qqwpick.databinding.ItemOrderLoadBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class OrderLoadListAdapter(list: MutableList<OrderLoadListBean>) :
    BaseQuickAdapter<OrderLoadListBean, BaseDataBindingHolder<ItemOrderLoadBinding>>(
        R.layout.item_order_load,
        list
    ), LoadMoreModule {
    override fun convert(
        holder: BaseDataBindingHolder<ItemOrderLoadBinding>,
        item: OrderLoadListBean
    ) {
        val binding = holder.dataBinding
        binding?.item = item
    }

}