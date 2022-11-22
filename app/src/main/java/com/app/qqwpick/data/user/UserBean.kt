package com.app.qqwpick.data.user

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class UserBean(
    @field:SerializedName("id") val id: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("storeId") val storeId: String,
    @field:SerializedName("phone") val phone: String,
    @field:SerializedName("jobNumber") val jobNumber: String,
    @field:SerializedName("storeUserRoleId") val storeUserRoleId: String,
    @field:SerializedName("storeName") val storeName: String,
    @field:SerializedName("token") val token: String,
    @field:SerializedName("isScanCodeAccept") val isScanCodeAccept: Boolean,
    @field:SerializedName("channelNo") val channelNo: String,
    @field:SerializedName("channelName") val channelName: String,
    @field:SerializedName("isGrabOrder") val isGrabOrder: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(storeId)
        parcel.writeString(phone)
        parcel.writeString(jobNumber)
        parcel.writeString(storeUserRoleId)
        parcel.writeString(storeName)
        parcel.writeString(token)
        parcel.writeByte(if (isScanCodeAccept) 1 else 0)
        parcel.writeString(channelNo)
        parcel.writeString(channelName)
        parcel.writeByte(if (isGrabOrder) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserBean> {
        override fun createFromParcel(parcel: Parcel): UserBean {
            return UserBean(parcel)
        }

        override fun newArray(size: Int): Array<UserBean?> {
            return arrayOfNulls(size)
        }
    }

}