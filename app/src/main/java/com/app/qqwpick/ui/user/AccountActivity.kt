package com.app.qqwpick.ui.user

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseResult
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.data.user.StoreBean
import com.app.qqwpick.databinding.ActivityAccountBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.ui.home.StoreListActivity
import com.app.qqwpick.util.SpUtils
import com.app.qqwpick.util.USER_JOB_NUMBER
import com.app.qqwpick.viewmodels.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountActivity : BaseVMActivity<ActivityAccountBinding>() {

    val viewModel: AccountViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_account
    }

    override fun initData() {
        super.initData()
        mBinding.tvNext.setOnClickListener {
            if (!getAccount().isNullOrEmpty() && getAccount().length > 3 && mBinding.ckAgree.isChecked) {
                viewModel.verifyAccount(getAccount())
            }
        }

        mBinding.ckAgree.setOnClickListener {
            mBinding.ckAgree.toggle()
            setEditBg()
        }

        mBinding.tvScreat.setOnClickListener {
            val intent = Intent(this, ScreatActivity::class.java)
            startActivity(intent)
        }

        mBinding.etAccount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                setEditBg()
            }
        })
    }

    private fun setEditBg() {
        mBinding.tvNext.isSelected = mBinding.ckAgree.isChecked && mBinding.etAccount.length() > 3
    }

    fun getAccount(): String {
        return mBinding.etAccount.text.toString().trim()
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.mStoreBean.observe(this, { handleResult(it) })
    }

    private fun handleResult(it: BaseResult<MutableList<StoreBean>>) {
        when (it.dataStatus) {
            DataStatus.STATE_LOADING ->
                showLoading()
            DataStatus.STATE_ERROR -> {
                dismissLoading()
                toast(it.exception!!.msg)
            }
            DataStatus.STATE_SUCCESS -> {
                dismissLoading()
                SpUtils.put(USER_JOB_NUMBER, mBinding.etAccount.text.toString().trim())
                val intent = Intent(this, StoreListActivity::class.java)
                var listBean = it.data?.toTypedArray()
                intent.putExtra("list", listBean)
                startActivity(intent)
            }
        }
    }

}