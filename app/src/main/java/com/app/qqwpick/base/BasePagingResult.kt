package com.app.qqwpick.base

data class BasePagingResult<T>(
    var startRow: Int,
    var endRow: Int,
    var pages: Int,
    var pageNum: Int, var pageSize: Int, var total: Int, var pageIndex: Int,
    var list: T

)