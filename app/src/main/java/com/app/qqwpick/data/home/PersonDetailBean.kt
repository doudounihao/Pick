package com.app.qqwpick.data.home

data class PersonDetailBean(
    val avgPickTime: Double,
    val currentDayHasPicked: Int,
    val currentDayOrders: Int,
    val currentMonthHasPicked: Int,
    val currentMonthOrders: Int,
    var onTimeRate: String
)