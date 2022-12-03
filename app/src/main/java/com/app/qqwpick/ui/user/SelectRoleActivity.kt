package com.app.qqwpick.ui.user

import android.content.Intent
import android.os.Bundle
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.databinding.ActivitySelectRoleBinding
import com.app.qqwpick.ui.home.HomeActivity
import com.app.qqwpick.util.*
import com.hjq.toast.ToastUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectRoleActivity : BaseVMActivity<ActivitySelectRoleBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_select_role
    }

    override fun initData() {
        super.initData()

        mBinding.tvSend.setOnClickListener {
            SpUtils.put(ROLE_TYPE, ROLE_SEND)
            goToPage()
        }
        mBinding.ivPs.setOnClickListener {
            SpUtils.put(ROLE_TYPE, ROLE_SEND)
            goToPage()
        }

        mBinding.tvPick.setOnClickListener {
            ToastUtils.show("敬请期待")
        }
        mBinding.ivJh.setOnClickListener {
//            SpUtils.put(ROLE_TYPE, ROLE_GRAB)
            ToastUtils.show("敬请期待")
        }
    }

    fun goToPage() {
        ActivityUtil.finishAllActivity()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}