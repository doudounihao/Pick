package com.app.qqwpick.ui.user

import android.content.Intent
import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SystemActivity : BaseVMActivity<ActivitySystemBinding>() {

    val viewModel: UserViewModel by viewModels()


    override fun initView(savedInstanceState: Bundle?) {
        mBinding.title.tvCenter.text = "系统设置"
        mBinding.tvAppVersion.text = BuildConfig.VERSION_NAME

        mBinding.title.tvLeft.setOnClickListener {
            finish()
        }

        mBinding.tvLoginOut.setOnClickListener {
            viewModel.loginOut()
        }
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.loginOut.observe(this, {
            handleResultLoginOut(it)
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

    override fun getLayoutId(): Int {
        return R.layout.activity_system
    }
}