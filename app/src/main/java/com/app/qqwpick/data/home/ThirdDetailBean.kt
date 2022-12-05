package com.app.qqwpick.data.home

import com.app.qqwpick.util.DateUtils
import java.io.Serializable

data class ThirdDetailBean(
    val orderNo: String,
    val channelOrderNo: String,
    val orderCreateTime: String,
    val expectEarliestSendTime: String,
    val expectLatestSendTime: String,
    val receiverName: String,
    val receiverPrivacyPhone: String,
    val address: String,
    val orderDetails: List<ThirdGoodsBean>
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
