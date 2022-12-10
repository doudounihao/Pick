package com.app.qqwpick.data.home

import com.app.qqwpick.util.DateUtils
import java.io.Serializable

data class OrderThirdListBean(
    val channelOrderNo: String,
    val dailySeq: Int,
    val expectEarliestSendTime: String,
    val expectLatestSendTime: String,
    val finishedTime: String,
    val merchantName: String,
    val merchantNo: String,
    val orderCreateTime: String,
    val orderNo: String,
    val receiverAccurateAddress: String,
    val receiverAddress: String,
    val receiverGender: String,
    val receiverName: String,
    val receiverPhone: String,
    val receiverPrivacyPhone: String,
    val remark: String,
    val sourceChannel: String,
    val status: Int,
    val storeName: String,
    val storeNo: String,
    val totalCommodityNum: Int,
    val totalWeight: Int,
    val receiverCity: String,
    val receiverProvince: String,
    val receiverDistrict: String,
    val receiverCoords: String,
    val deliveryInfo: DeliveryInfoBean
) : Serializable {

    @JvmName("getSendTime")
    fun getSendTime(): String {
        val sameHour: Boolean =
            DateUtils.isSameHour(expectEarliestSendTime, expectLatestSendTime)
        if (sameHour) {
            return DateUtils.getDateTime(expectEarliestSendTime)
        }
        val sameDay: Boolean =
            DateUtils.isSameDay(expectEarliestSendTime, expectLatestSendTime)
        return if (sameDay) {
            DateUtils.getDateTime(expectEarliestSendTime).toString() + "至" + DateUtils.getTime(
                expectLatestSendTime
            )
        } else DateUtils.getDateTime(expectEarliestSendTime)
            .toString() + "至" + DateUtils.getDateTime(expectLatestSendTime)
    }
}