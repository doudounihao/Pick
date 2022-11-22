package com.app.qqwpick.data.user

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StoreBean(
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("storeNo") val storeNo: String,
    @field:SerializedName("contactTel") val contactTel: String,
    var isSelect: Boolean
) : Serializable
