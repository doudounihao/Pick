package com.app.qqwpick.net

//异常结果类，错误码和错误信息
class ResultException(var errCode: String, var msg: String) : Exception(msg)
