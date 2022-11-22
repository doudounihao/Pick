package com.app.qqwpick.data.home

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
)