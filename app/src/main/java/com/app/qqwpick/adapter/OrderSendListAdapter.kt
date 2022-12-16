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
        if (item.serialNum.contains(" ")) {
            val s: Array<String> = item.serialNum.split(" ").toTypedArray()
            var s1 = s[0]
            s1 = s1.replace("#".toRegex(), "")
            binding?.tvNum?.setText(s1)
            val str = StringBuilder()
            str.replace(0, str.length, "(").append(s[1]).append(")")
            binding?.txtItemOrderListFragmentSerialDate?.setText(str.toString())
        }
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