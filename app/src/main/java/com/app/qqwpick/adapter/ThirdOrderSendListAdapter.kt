package com.app.qqwpick.adapter

import android.view.View
import com.app.qqwpick.R
import com.app.qqwpick.data.home.OrderThirdListBean
import com.app.qqwpick.databinding.ItemThirdOrderSendBinding
import com.app.qqwpick.util.DateUtils
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
        val str = StringBuilder()
        binding?.txtItemOrderListFragmentSerialDate?.text =
            str.replace(0, str.length, "(").append(DateUtils.getMonthAndDay(item.orderCreateTime))
                .append(")").toString()
        if (item.sourceChannel.contains("美团")) {
            binding?.ivOrderStatus?.setImageResource(R.mipmap.icon_number_mt_right_list)
        } else if (item.sourceChannel.contains("京东")) {
            binding?.ivOrderStatus?.setImageResource(R.mipmap.icon_number_jd_right_list)
        } else if (item.sourceChannel.contains("饿了么")) {
            binding?.ivOrderStatus?.setImageResource(R.mipmap.icon_number_elm_right_list)
        } else if (item.sourceChannel.contains("企业购")) {
            binding?.ivOrderStatus?.setImageResource(R.mipmap.icon_number_qyg_list)
        }
        val timeStr: Int = DateUtils.calculateTime(item.expectEarliestSendTime)
        if (timeStr < 120) {
            binding?.deliveryItemOrderListFragmentStatus?.setVisibility(View.VISIBLE)
            binding?.deliveryItemOrderListFragmentStatus?.setData(
                item.expectEarliestSendTime,
                null,
                DateUtils.getCurrentTime1()
            )
        } else {
            binding?.deliveryItemOrderListFragmentStatus?.setVisibility(View.GONE)
        }
    }
}