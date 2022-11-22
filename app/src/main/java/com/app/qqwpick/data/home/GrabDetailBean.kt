package com.app.qqwpick.data.home

data class GrabDetailBean(
    val bespokeTimeFrom: String,
    val bespokeTimeTo: String,
    val isTimelyOrder: Boolean,
    val orderId: Int,
    val orderNo: String,
    val receiverAddress: String,
    val serialNumber: String,
    val storeAddress: String,
    val storeId: Int,
    val storeName: String,
    val storeNo: String
)