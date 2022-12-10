package com.app.qqwpick.adapter

import com.app.qqwpick.R
import com.app.qqwpick.data.home.GoodsBean
import com.app.qqwpick.databinding.ItemGoodsBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class GoodsAdapter(list: MutableList<GoodsBean>) :
    BaseQuickAdapter<GoodsBean, BaseDataBindingHolder<ItemGoodsBinding>>(
        R.layout.item_goods,
        list
    ) {
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsBinding>, item: GoodsBean) {
        val binding = holder.dataBinding
        binding?.item = item
        Glide.with(context)
            .applyDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.ic_login)
                    .transform(RoundedCorners(8))
            )
            .load(item.commodityUrl).into(binding?.ivGoods!!)
    }

}