package com.app.qqwpick.data.home

import com.app.qqwpick.util.DateUtils
import java.io.Serializable

data class OrderLoadListBean(
    val bespokeTimeFrom: String,
    val bespokeTimeTo: String,
    val lat: String,
    val lng: String,
    val orderNo: String,
    val orderPayTime: String,
    val receiverAddress: String,
    val receiverCity: String,
    val receiverDistrict: String,
    val receiverState: String,
    val serialNum: String
) : Serializable {

    @JvmName("getSendTime")
    fun getSendTime(): String {
        val sameHour: Boolean =
            DateUtils.isSameHour(bespokeTimeFrom, bespokeTimeTo)
        if (sameHour) {
            return DateUtils.getDateTime(bespokeTimeFrom)
        }
        val sameDay: Boolean =
            DateUtils.isSameDay(bespokeTimeFrom, bespokeTimeTo)
        return if (sameDay) {
            DateUtils.getDateTime(bespokeTimeFrom).toString() + "至" + DateUtils.getTime(
                bespokeTimeTo
            )
        } else DateUtils.getDateTime(bespokeTimeFrom)
            .toString() + "至" + DateUtils.getDateTime(bespokeTimeTo)
    }
}