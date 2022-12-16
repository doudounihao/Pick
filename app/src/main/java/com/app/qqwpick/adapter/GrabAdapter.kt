package com.app.qqwpick.adapter

import com.app.qqwpick.R
import com.app.qqwpick.data.home.GrabListBean
import com.app.qqwpick.databinding.ItemGrabBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class GrabAdapter(list: MutableList<GrabListBean>) :
    BaseQuickAdapter<GrabListBean, BaseDataBindingHolder<ItemGrabBinding>>(
        R.layout.item_grab,
        list
    ), LoadMoreModule {
    override fun convert(holder: BaseDataBindingHolder<ItemGrabBinding>, item: GrabListBean) {
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