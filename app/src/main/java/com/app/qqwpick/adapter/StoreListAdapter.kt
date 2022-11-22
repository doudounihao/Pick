package com.app.qqwpick.adapter

import com.app.qqwpick.R
import com.app.qqwpick.data.user.StoreBean
import com.app.qqwpick.databinding.ItemStoreListBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class StoreListAdapter(list: MutableList<StoreBean>) :
    BaseQuickAdapter<StoreBean, BaseDataBindingHolder<ItemStoreListBinding>>(
        R.layout.item_store_list,
        list
    ) {

    override fun convert(holder: BaseDataBindingHolder<ItemStoreListBinding>, item: StoreBean) {
        holder.dataBinding?.tvName?.text = item.name
        holder.dataBinding?.ckTv?.isChecked = item.isSelect
    }

}