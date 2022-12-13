package com.app.qqwpick.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.app.qqwpick.BuildConfig
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseResult
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.databinding.ActivitySystemBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.ActivityUtil
import com.app.qqwpick.util.ROLE_TYPE
import com.app.qqwpick.util.SpUtils
import com.app.qqwpick.viewmodels.UserViewModel
import com.hjq.toast.ToastUtils
import constant.UiType
import dagger.hilt.android.AndroidEntryPoint
import listener.OnInitUiListener
import model.UiConfig
import model.UpdateConfig
import update.UpdateAppUtils

@AndroidEntryPoint
class SystemActivity : BaseVMActivity<ActivitySystemBinding>() {

    val viewModel: UserViewModel by viewModels()
    var versionCode: String = ""
    var oldCode: String = ""

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.title.tvCenter.text = "系统设置"
        mBinding.tvAppVersion.text = BuildConfig.VERSION_NAME

        mBinding.title.tvLeft.setOnClickListener {
            finish()
        }

        mBinding.tvLoginOut.setOnClickListener {
            viewModel.loginOut()
        }

        mBinding.tvUpdate.setOnClickListener {
            viewModel.getVersion()
        }
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.loginOut.observe(this, {
            handleResultLoginOut(it)
        })

        viewModel.versionBean.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_LOADING ->
                    showLoading()
                DataStatus.STATE_ERROR -> {
                    dismissLoading()
                    toast(it.exception!!.msg)
                }
                DataStatus.STATE_SUCCESS -> {
                    dismissLoading()
                    versionCode = it.data?.newver.toString()
                    oldCode = it.data?.oldver.toString()
                    var isUpdate = false
                    if (compareVersion(oldCode, BuildConfig.VERSION_NAME) > 0) {
                        isUpdate = true
                    }
                    if (compareVersion(versionCode, BuildConfig.VERSION_NAME) > 0) {
                        updateApp(
                            it.data?.address.toString(),
                            it.data?.content.toString(),
                            it.data?.title.toString(),
                            isUpdate
                        )
                    } else {
                        ToastUtils.show("当前已是最新版本")
                    }
                }
            }
        })
    }

    fun handleResultLoginOut(it: BaseResult<Any>) {
        when (it.dataStatus) {
            DataStatus.STATE_LOADING ->
                showLoading()
            DataStatus.STATE_ERROR -> {
                dismissLoading()
                toast(it.exception!!.msg)
            }
            DataStatus.STATE_SUCCESS -> {
                ActivityUtil.finishAllActivity()
                SpUtils.put(ROLE_TYPE, "")
                var intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
                dismissLoading()
            }
        }
    }

    fun compareVersion(version1: String, version2: String): Int {
        if (version1 == version2) {
            return 0
        }
        val version1Array = version1.trim().split(".")
        val version2Array = version2.trim().split(".")
        var index = 0
        var minLen = Math.min(version1Array.size, version2Array.size)
        var diff = 0
        while (index < minLen) {
            try {
                diff = version1Array[index].toInt() - version2Array[index].toInt()
            } catch (e: NumberFormatException) {
                diff = version1Array[index].compareTo(version2Array[index])
            }
            if (diff != 0) {
                break
            }
            index++
        }

        if (diff == 0) {
            if (version1Array.size > minLen) {
                for (i in index until version1Array.size) {
                    try {
                        if (version1Array[i].isNotBlank() && version1Array[i].toInt() > 0) {
                            return 1
                        }
                    } catch (e: NumberFormatException) {
                        return 1
                    }
                }
                for (i in index until version2Array.size) {
                    try {
                        if (version2Array[i].isNotBlank() && version2Array[i].toInt() > 0) {
                            return -1
                        }
                    } catch (e: NumberFormatException) {
                        return -1
                    }

                }
            } else {

            }
        } else {
            return if (diff > 0) 1 else -1
        }
        return 0
    }

    private fun updateApp(
        apkUrl: String,
        updateContent: String,
        updateTitle: String,
        isUpdate: Boolean
    ) {
        UpdateAppUtils
            .getInstance()
            .apkUrl(apkUrl)
            .updateConfig(
                UpdateConfig(
                    force = isUpdate
                )
            )
            .uiConfig(
                UiConfig(
                    uiType = UiType.CUSTOM,
                    customLayoutId = R.layout.view_update_dialog_custom
                )
            )
            .setOnInitUiListener(object : OnInitUiListener {
                override fun onInitUpdateUi(
                    view: View?,
                    updateConfig: UpdateConfig,
                    uiConfig: UiConfig
                ) {
                    view?.findViewById<TextView>(R.id.tv_update_title)?.text = updateTitle
                    view?.findViewById<TextView>(R.id.tv_update_content)?.text = updateContent
                }
            })
            .update()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_system
    }
}