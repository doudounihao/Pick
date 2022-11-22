package com.app.qqwpick.data.home

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StoreRoleBean(
    @field:SerializedName("storeUserId") val storeUserId: String,
    @field:SerializedName("storeUserRoleId") val storeUserRoleId: Int,//1 配送员 2 拣货员
    @field:SerializedName("roleName") val roleName: String
) : Serializable