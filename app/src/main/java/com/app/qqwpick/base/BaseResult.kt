package com.app.qqwpick.base

import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.net.ResultException
import com.google.gson.annotations.SerializedName


//返回的基础model
class BaseResult<T> {
    //错误码0为正确
    var code: String = "-1"

    var msg: String? = null

    var message: String? = null

    //返回数据
    var data: T? = null
        private set

    //数据状态
    var dataStatus: DataStatus? = null

    //当STATE_ERROR状态的时候这个才会有值
    var exception: ResultException? = null
}