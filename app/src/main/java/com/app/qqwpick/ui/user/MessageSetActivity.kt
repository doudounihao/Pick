package com.app.qqwpick.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.databinding.ActivityMessageSetBinding
import com.app.qqwpick.util.*

class MessageSetActivity : BaseVMActivity<ActivityMessageSetBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.title.tvCenter.setText("消息提醒设置")

        mBinding.title.tvLeft.setOnClickListener {
            finish()
        }

        mBinding.toggleMesRemindNewOrder.isChecked =
            SpUtils.getBoolean(NEW_ORDER_REMIND_SWITCH) ?: false
        mBinding.layoutMesRemindNewOrderRemind.isVisible =
            SpUtils.getBoolean(NEW_ORDER_REMIND_SWITCH) ?: false
        mBinding.toggleMesRemindNewOrder.setOnClickListener {
            if (mBinding.toggleMesRemindNewOrder.isChecked) {
                mBinding.layoutMesRemindNewOrderRemind.isVisible = true
                SpUtils.put(NEW_ORDER_REMIND_SWITCH, true)
            } else {
                mBinding.layoutMesRemindNewOrderRemind.isVisible = false
                SpUtils.put(NEW_ORDER_REMIND_SWITCH, false)
            }
        }
        mBinding.layoutMesRemindNewOrderRemind.setOnClickListener {
            var intent = Intent(this, OrderRemindActivity::class.java)
            intent.putExtra("type", NEW_ORDER_REMIND_SWITCH_TYPE)
            startActivity(intent)
        }

        mBinding.toggleMesRemindGrabOrder.isChecked =
            SpUtils.getBoolean(GRAB_ORDER_REMIND_SWITCH) ?: false
        mBinding.layoutMesRemindGrabOrderRemind.isVisible =
            SpUtils.getBoolean(GRAB_ORDER_REMIND_SWITCH) ?: false
        mBinding.toggleMesRemindGrabOrder.setOnClickListener {
            if (mBinding.toggleMesRemindGrabOrder.isChecked) {
                mBinding.layoutMesRemindGrabOrderRemind.isVisible = true
                SpUtils.put(GRAB_ORDER_REMIND_SWITCH, true)
            } else {
                mBinding.layoutMesRemindGrabOrderRemind.isVisible = false
                SpUtils.put(GRAB_ORDER_REMIND_SWITCH, false)
            }
        }
        mBinding.layoutMesRemindGrabOrderRemind.setOnClickListener {
            var intent = Intent(this, OrderRemindActivity::class.java)
            intent.putExtra("type", GRAB_ORDER_REMIND_SWITCH_TYPE)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        mBinding.tvNewOrder.setText(SpUtils.getString(NEW_ORDER_REMIND_SWITCH_TYPE))
        mBinding.tvGrabSend.setText(SpUtils.getString(GRAB_ORDER_REMIND_SWITCH_TYPE))
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_message_set
    }
}