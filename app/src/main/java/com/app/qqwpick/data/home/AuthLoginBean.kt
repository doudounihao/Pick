package com.app.qqwpick.data.home

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AuthLoginBean(
    @field:SerializedName("id") val id: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("storeNo") val storeNo: String,
    @field:SerializedName("phone") val phone: String,
    @field:SerializedName("onTimeNum") val onTimeNum: String,
    @field:SerializedName("overTimeNum") val overTimeNum: String,
    @field:SerializedName("storeUserRoleRelations") val storeUserRoleRelations: List<StoreRoleBean>
):Serializable
