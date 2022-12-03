package com.app.qqwpick.adapter

import com.app.qqwpick.R
import com.app.qqwpick.data.home.ThirdGoodsBean
import com.app.qqwpick.databinding.ItemThirdGoodsBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class ThirdGoodsAdapter(list: MutableList<ThirdGoodsBean>) :
    BaseQuickAdapter<ThirdGoodsBean, BaseDataBindingHolder<ItemThirdGoodsBinding>>(
        R.layout.item_third_goods,
        list
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemThirdGoodsBinding>,
        item: ThirdGoodsBean
    ) {
        val binding = holder.dataBinding
        binding?.item = item
        Glide.with(context)
            .applyDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.ic_login)
                    .centerCrop()
            )
            .load(item.commodityPic).into(binding?.ivGoods!!)
    }

}