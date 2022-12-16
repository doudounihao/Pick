package com.app.qqwpick.ui.user

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseResult
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.data.home.AuthLoginBean
import com.app.qqwpick.data.user.UserBean
import com.app.qqwpick.databinding.ActivityLoginBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.SpUtils
import com.app.qqwpick.util.USER_BEAN
import com.app.qqwpick.viewmodels.LoginViewModel
import com.galenleo.widgets.CodeInputView
import com.hjq.toast.ToastUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseVMActivity<ActivityLoginBinding>(), CodeInputView.OnTextChangListener {

    val viewModel: LoginViewModel by viewModels()
    lateinit var authLoginBean: AuthLoginBean
    var t: Long = 60 * 1000 //定义倒计时长 60s

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData() {
        super.initData()
        authLoginBean = intent.getSerializableExtra("bean") as AuthLoginBean

        mBinding.tvBack.setOnClickListener { finish() }

        mBinding.tvPhone.setText("至" + authLoginBean.phone)

        mBinding.clCode.setOnClickListener {
            viewModel.getPhoneCode(authLoginBean)
            countDownTimer.start()
        }

        mBinding.etCode.setListener(this)
        mBinding.tvLogin.setOnClickListener {
            if (mBinding.tvLogin.isSelected) {
                viewModel.loginByCode(authLoginBean, getUserCode())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        countDownTimer.cancel()
        setCodeFinish()
    }

    var countDownTimer = object : CountDownTimer(t, 1000) {
        override fun onFinish() {
            setCodeFinish()
        }

        override fun onTick(millisUntilFinished: Long) {
            var second = millisUntilFinished / 1000 % 60
            mBinding.tvCode.setText("" + second + "s")
            mBinding.tvCode.setTextColor(resources.getColor(R.color.colorGray9))
            mBinding.tvCode.setBackgroundResource(R.drawable.tv_next_grey_bg)
        }
    }

    fun setCodeFinish() {
        mBinding.tvCode.setText("获取验证码")
        mBinding.tvCode.setTextColor(resources.getColor(R.color.white))
        mBinding.tvCode.setBackgroundResource(R.drawable.tv_next_blue_bg)
    }

    fun getUserCode(): String {
        return mBinding.etCode.text.toString().trim()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.userBean.observe(this, { handleResult(it) })

        viewModel.userCode.observe(this, {
            handleResultCode(it)
        })
    }

    private fun handleResult(it: BaseResult<UserBean>) {
        when (it.dataStatus) {
            DataStatus.STATE_LOADING ->
                showLoading()
            DataStatus.STATE_ERROR -> {
                dismissLoading()
                toast(it.exception!!.msg)
            }
            DataStatus.STATE_SUCCESS -> {
                dismissLoading()
                SpUtils.put(USER_BEAN, it.data)
                val intent = Intent(this, SelectRoleActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun handleResultCode(it: BaseResult<String>) {
        when (it.dataStatus) {
            DataStatus.STATE_LOADING ->
                showLoading()
            DataStatus.STATE_ERROR -> {
                dismissLoading()
                toast(it.exception!!.msg)
            }
            DataStatus.STATE_SUCCESS -> {
                dismissLoading()
                ToastUtils.show("发送成功")
            }
        }
    }

    override fun afterTextChanged(text: String?) {
        if (text?.length ?: 0 > 3) {
            mBinding.tvLogin.isSelected = true
        }
    }
}