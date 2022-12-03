package com.app.qqwpick.ui.user

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

        mBinding.toggleMesRemindThirdOrder.isChecked =
            SpUtils.getBoolean(THIRD_ORDER_REMIND_SWITCH) ?: false
        mBinding.layoutMesRemindThirdOrderRemind.isVisible =
            SpUtils.getBoolean(THIRD_ORDER_REMIND_SWITCH) ?: false
        mBinding.toggleMesRemindThirdOrder.setOnClickListener {
            if (mBinding.toggleMesRemindThirdOrder.isChecked) {
                mBinding.layoutMesRemindThirdOrderRemind.isVisible = true
                SpUtils.put(THIRD_ORDER_REMIND_SWITCH, true)
            } else {
                mBinding.layoutMesRemindThirdOrderRemind.isVisible = false
                SpUtils.put(THIRD_ORDER_REMIND_SWITCH, false)
            }
        }
        mBinding.layoutMesRemindThirdOrderRemind.setOnClickListener {
            var intent = Intent(this, OrderRemindActivity::class.java)
            intent.putExtra("type", THIRD_ORDER_REMIND_SWITCH_TYPE)
            startActivity(intent)
        }


        mBinding.toggleMesRemindUnsend.isChecked =
            SpUtils.getBoolean(ORDER_UNSEND_MINUTE_SWITCH) ?: false
        mBinding.layoutMesRemindUnsend.isVisible =
            SpUtils.getBoolean(ORDER_UNSEND_MINUTE_SWITCH) ?: false
        mBinding.toggleMesRemindUnsend.setOnClickListener {
            if (mBinding.toggleMesRemindUnsend.isChecked) {
                mBinding.layoutMesRemindUnsend.isVisible = true
                SpUtils.put(ORDER_UNSEND_MINUTE_SWITCH, true)
            } else {
                mBinding.layoutMesRemindUnsend.isVisible = false
                SpUtils.put(ORDER_UNSEND_MINUTE_SWITCH, false)
            }
        }
        mBinding.etUnsend.setText(SpUtils.getString(ORDER_UNSEND_MINUTE) ?: "5")
        mBinding.etUnsend.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                SpUtils.put(ORDER_UNSEND_MINUTE, s.toString())
            }
        })

        mBinding.toggleMesRemindUnsendThird.isChecked =
            SpUtils.getBoolean(THIRD_ORDER_UNSEND_MINUTE_SWITCH) ?: false
        mBinding.layoutMesRemindUnsendThird.isVisible =
            SpUtils.getBoolean(THIRD_ORDER_UNSEND_MINUTE_SWITCH) ?: false
        mBinding.toggleMesRemindUnsendThird.setOnClickListener {
            if (mBinding.toggleMesRemindUnsendThird.isChecked) {
                mBinding.layoutMesRemindUnsendThird.isVisible = true
                SpUtils.put(THIRD_ORDER_UNSEND_MINUTE_SWITCH, true)
            } else {
                mBinding.layoutMesRemindUnsendThird.isVisible = false
                SpUtils.put(THIRD_ORDER_UNSEND_MINUTE_SWITCH, false)
            }
        }
        mBinding.etUnsendThird.setText(SpUtils.getString(THIRD_ORDER_UNSEND_MINUTE) ?: "5")
        mBinding.etUnsendThird.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                SpUtils.put(THIRD_ORDER_UNSEND_MINUTE, s.toString())
            }
        })

    }

    override fun onResume() {
        super.onResume()
        mBinding.tvNewOrder.setText(SpUtils.getString(NEW_ORDER_REMIND_SWITCH_TYPE))
        mBinding.tvGrabSend.setText(SpUtils.getString(GRAB_ORDER_REMIND_SWITCH_TYPE))
        mBinding.tvThirdSend.setText(SpUtils.getString(THIRD_ORDER_REMIND_SWITCH_TYPE))
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_message_set
    }
}