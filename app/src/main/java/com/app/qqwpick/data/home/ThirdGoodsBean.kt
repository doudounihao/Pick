package com.app.qqwpick.data.home

import com.app.qqwpick.util.MathUtils
import java.io.Serializable

data class ThirdGoodsBean(
    val commodityName: String,
    val originPrice: String,
    val num: String,
    val adjustNum: String,
    val commodityPic: String
) : Serializable {

    @JvmName("getSinglePrice")
    fun getSinglePrice(): String {
        try {
            return if (0.equals(MathUtils.add(num, adjustNum))) {
                MathUtils.divide(originPrice, if (num.equals("0")) "1" else num)
            } else {
                MathUtils.divide(originPrice, MathUtils.add(num, adjustNum))
            }
        } catch (e: Exception) {
        }
        return "0"
    }
}