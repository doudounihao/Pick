package com.app.qqwpick.data.home

import com.app.qqwpick.util.DateUtils
import java.io.Serializable

data class OrderListBean(
    val bespokeTimeFrom: String,
    val bespokeTimeTo: String,
    val customerLogo: String,
    val fullName: String,
    val id: Int,
    val isTimelyOrder: Boolean,
    val lat: String,
    val lng: String,
    val orderCreateTime: String,
    val orderId: String,
    val orderNo: String,
    val orderPayTime: String,
    val orderSplitNo: String,
    val outboundStatus: Int,
    val phone: String,
    val receiverAddress: String,
    val receiverCity: String,
    val receiverDistrict: String,
    val receiverMobile: String,
    val receiverName: String,
    val receiverState: String,
    var deliveryStartTime: String?,
    val serialNum: String,
    val storeId: String,
    val vipCardNo: String,
    val orderSplitDetails: List<GoodsBean>
) : Serializable {

    @JvmName("getSendTime")
    fun getSendTime(): String {
        val sameHour: Boolean =
            DateUtils.isSameHour(bespokeTimeTo, bespokeTimeFrom)
        if (sameHour) {
            return DateUtils.getDateTime(bespokeTimeTo)
        }
        val sameDay: Boolean =
            DateUtils.isSameDay(bespokeTimeTo, bespokeTimeFrom)
        return if (sameDay) {
            DateUtils.getDateTime(bespokeTimeFrom).toString() + "至" + DateUtils.getTime(
                bespokeTimeTo
            )
        } else DateUtils.getDateTime(bespokeTimeFrom)
            .toString() + "至" + DateUtils.getDateTime(bespokeTimeTo)
    }
}


