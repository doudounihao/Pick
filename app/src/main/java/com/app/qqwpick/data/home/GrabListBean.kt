package com.app.qqwpick.data.home


data class GrabListBean(
    val bespokeTimeFrom: String,
    val bespokeTimeTo: String,
    val id: Int,
    val lat: String,
    val lng: String,
    val orderNo: String,
    val orderPayTime: String,
    val receiverAddress: String,
    val receiverCity: String,
    val receiverDistrict: String,
    val receiverState: String,
    val serialNum: String
)