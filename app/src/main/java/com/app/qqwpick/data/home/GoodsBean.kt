package com.app.qqwpick.data.home

import java.io.Serializable

data class GoodsBean(
    val categoryName: String,
    val categoryNo: String,
    val commodityId: Int,
    val commodityName: String,
    val commodityUrl: String,
    val num: Int,
    val price: String
) : Serializable