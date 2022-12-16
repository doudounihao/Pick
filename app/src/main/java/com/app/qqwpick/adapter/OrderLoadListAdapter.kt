package com.app.qqwpick.adapter

import android.text.TextUtils
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
        if (item.serialNum.contains(" ")) {
            val s: Array<String> = item.serialNum.split(" ").toTypedArray()
            var s1 = s[0]
            s1 = s1.replace("#".toRegex(), "")
            binding?.tvNum?.setText(s1)
            val str = StringBuilder()
            str.replace(0, str.length, "(").append(s[1]).append(")")
            binding?.txtItemOrderListFragmentSerialDate?.setText(str.toString())
        }
    }

}