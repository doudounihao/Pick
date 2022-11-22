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
    }

}