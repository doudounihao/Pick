package com.app.qqwpick.ui.user

import android.content.Intent
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseResult
import com.app.qqwpick.base.BaseVMFragment
import com.app.qqwpick.data.home.PersonDetailBean
import com.app.qqwpick.data.user.UserBean
import com.app.qqwpick.databinding.FragmentUserBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.MAP_OPEN
import com.app.qqwpick.util.SpUtils
import com.app.qqwpick.util.USER_BEAN
import com.app.qqwpick.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : BaseVMFragment<FragmentUserBinding>() {

    val viewModel: UserViewModel by viewModels()

    override fun initView(view: View) {
        mBinding.tvName.text = SpUtils.getParcelable<UserBean>(USER_BEAN)?.name
        mBinding.toggleMap.isChecked =
            SpUtils.getBoolean(MAP_OPEN) ?: false

        mBinding.tvTodayTip.setOnClickListener {
            var intent = Intent(requireContext(), FinishOrderActivity::class.java)
            intent.putExtra("type", "today")
            startActivity(intent)
        }

        mBinding.tvMonthTip.setOnClickListener {
            var intent = Intent(requireContext(), FinishOrderActivity::class.java)
            intent.putExtra("month", "month")
            startActivity(intent)
        }

        mBinding.toggleMap.setOnClickListener {
            if (mBinding.toggleMap.isChecked) {
                SpUtils.put(MAP_OPEN, true)
            } else {
                SpUtils.put(MAP_OPEN, false)
            }
        }

        mBinding.tvEwmTip.setOnClickListener {
            var intent = Intent(requireContext(), QrCodeActvity::class.java)
            startActivity(intent)
        }

        mBinding.tvMessageTip.setOnClickListener {
            var intent = Intent(requireContext(), MessageSetActivity::class.java)
            startActivity(intent)
        }

        mBinding.tvSystem.setOnClickListener {
            var intent = Intent(requireContext(), SystemActivity::class.java)
            startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_user
    }

    override fun initData() {
        super.initData()
        viewModel.getPersonDetail()
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.userBean.observe(this, {
            handleResult(it)
        })
    }

    private fun handleResult(it: BaseResult<PersonDetailBean>) {
        when (it.dataStatus) {
            DataStatus.STATE_LOADING ->
                showLoading()
            DataStatus.STATE_ERROR -> {
                dismissLoading()
                toast(it.exception!!.msg)
            }
            DataStatus.STATE_SUCCESS -> {
                mBinding.item = it.data
                mBinding.tvPercent.text = "配送准时率：" + it.data?.onTimeRate
                dismissLoading()
            }
        }
    }


}