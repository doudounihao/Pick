package com.app.qqwpick.adapter

import com.app.qqwpick.R
import com.app.qqwpick.data.home.OrderThirdListBean
import com.app.qqwpick.databinding.ItemThirdOrderFinishBinding
import com.app.qqwpick.util.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class ThirdFinishOrderListAdapter(list: MutableList<OrderThirdListBean>) :
    BaseQuickAdapter<OrderThirdListBean, BaseDataBindingHolder<ItemThirdOrderFinishBinding>>(
        R.layout.item_third_order_finish,
        list
    ), LoadMoreModule {
    override fun convert(
        holder: BaseDataBindingHolder<ItemThirdOrderFinishBinding>,
        item: OrderThirdListBean
    ) {
        val binding = holder.dataBinding
        binding?.item = item
        val str = StringBuilder()
        binding?.txtItemOrderListFragmentSerialDate?.text = str.replace(0, str.length, "(")
            .append(DateUtils.getMonthAndDay(item.orderCreateTime)).append(")").toString()
    }

}