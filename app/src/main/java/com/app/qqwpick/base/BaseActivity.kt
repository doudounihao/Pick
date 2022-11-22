package com.app.qqwpick.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.qqwpick.util.ActivityUtil
import com.app.qqwpick.util.StatusBarUtil
import com.hjq.toast.ToastUtils

open class BaseActivity : AppCompatActivity() {
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLoadingDialog = LoadingDialog(this)
        ActivityUtil.addActivity(this)
        StatusBarUtil.setTransparentForWindow(this)
        StatusBarUtil.setDarkMode(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityUtil.removeActivity(this)

    }

    /**
     * show 加载中
     */
    fun showLoading() {
        mLoadingDialog.showDialog(this)
    }

    /**
     * dismiss loading dialog
     */
    fun dismissLoading() {
        mLoadingDialog.dismissDialog()
    }

    fun toast(message: String) {
        ToastUtils.show(message)
    }

}