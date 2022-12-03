package com.app.qqwpick.ui.user

import android.os.Bundle
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.databinding.ActivityOrderRemindBinding
import com.app.qqwpick.util.*

class OrderRemindActivity : BaseVMActivity<ActivityOrderRemindBinding>() {

    var type = ""
    var text = ""

    override fun initView(savedInstanceState: Bundle?) {
        type = intent.getStringExtra("type") ?: ""
        if (type.equals(NEW_ORDER_REMIND_SWITCH_TYPE)) {
            mBinding.title.tvCenter.setText("到家到店新订单消息设置")
            text = SpUtils.getString(NEW_ORDER_REMIND_SWITCH_TYPE) ?: ""
        } else if (type.equals(GRAB_ORDER_REMIND_SWITCH_TYPE)) {
            mBinding.title.tvCenter.setText("到家到店抢单消息设置")
            text = SpUtils.getString(GRAB_ORDER_REMIND_SWITCH_TYPE) ?: ""
        } else {
            mBinding.title.tvCenter.setText("三方订单新订单消息设置")
            text = SpUtils.getString(THIRD_ORDER_REMIND_SWITCH_TYPE) ?: ""
        }

        mBinding.title.tvLeft.setOnClickListener {
            finish()
        }

        if (text.equals(REMIND_TYPE_ONE)) {
            mBinding.cbSoundSystem.isChecked = true
        } else if (text.equals(REMIND_TYPE_TWO)) {
            mBinding.cbSoundPeople.isChecked = true
        } else {
            mBinding.cbSoundVibration.isChecked = true
        }

        mBinding.layoutSoundSystem.setOnClickListener {
            mBinding.cbSoundSystem.isChecked = true
            mBinding.cbSoundPeople.isChecked = false
            mBinding.cbSoundVibration.isChecked = false
            if (type.equals(NEW_ORDER_REMIND_SWITCH_TYPE)) {
                SpUtils.put(NEW_ORDER_REMIND_SWITCH_TYPE, REMIND_TYPE_ONE)
            } else if (type.equals(GRAB_ORDER_REMIND_SWITCH_TYPE)) {
                SpUtils.put(GRAB_ORDER_REMIND_SWITCH_TYPE, REMIND_TYPE_ONE)
            } else {
                SpUtils.put(THIRD_ORDER_REMIND_SWITCH_TYPE, REMIND_TYPE_ONE)
            }
        }

        mBinding.layoutSoundPeople.setOnClickListener {
            mBinding.cbSoundSystem.isChecked = false
            mBinding.cbSoundPeople.isChecked = true
            mBinding.cbSoundVibration.isChecked = false
            if (type.equals(NEW_ORDER_REMIND_SWITCH_TYPE)) {
                SpUtils.put(NEW_ORDER_REMIND_SWITCH_TYPE, REMIND_TYPE_TWO)
            } else if (type.equals(GRAB_ORDER_REMIND_SWITCH_TYPE)) {
                SpUtils.put(GRAB_ORDER_REMIND_SWITCH_TYPE, REMIND_TYPE_TWO)
            } else {
                SpUtils.put(THIRD_ORDER_REMIND_SWITCH_TYPE, REMIND_TYPE_TWO)
            }
        }

        mBinding.layoutSoundVibration.setOnClickListener {
            mBinding.cbSoundSystem.isChecked = false
            mBinding.cbSoundPeople.isChecked = false
            mBinding.cbSoundVibration.isChecked = true
            if (type.equals(NEW_ORDER_REMIND_SWITCH_TYPE)) {
                SpUtils.put(NEW_ORDER_REMIND_SWITCH_TYPE, REMIND_TYPE_THREE)
            } else if (type.equals(GRAB_ORDER_REMIND_SWITCH_TYPE)) {
                SpUtils.put(GRAB_ORDER_REMIND_SWITCH_TYPE, REMIND_TYPE_THREE)
            } else {
                SpUtils.put(THIRD_ORDER_REMIND_SWITCH_TYPE, REMIND_TYPE_THREE)
            }
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_remind
    }
}