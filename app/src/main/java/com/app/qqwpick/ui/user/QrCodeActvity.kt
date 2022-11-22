package com.app.qqwpick.ui.user

import android.graphics.Bitmap
import android.os.Bundle
import com.app.qqwpick.MainApplication
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.databinding.ActivityQrCodeBinding
import com.app.qqwpick.util.ANDROID_EWM_URL
import com.app.qqwpick.util.IOS_EWM
import com.app.qqwpick.util.SpUtils
import com.app.qqwpick.util.Utils

class QrCodeActvity : BaseVMActivity<ActivityQrCodeBinding>() {

    var isAndoird = true
    lateinit var bitmap: Bitmap

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.title.tvCenter.setText("分享二维码")

        mBinding.title.tvLeft.setOnClickListener {
            finish()
        }

        mBinding.tvChange.setOnClickListener {
            if (isAndoird) {
                isAndoird = false
                mBinding.tvName.setText("Ios下载二维码")
            } else {
                isAndoird = true
                mBinding.tvName.setText("Andoird下载二维码")
            }
            setErm()
        }

        setErm()
    }

    private fun setErm() {
        try {
            if (isAndoird) {
                bitmap = Utils.createQRCode(SpUtils.getString(ANDROID_EWM_URL), 150)
                mBinding.ivQrCode.setImageBitmap(bitmap)
            } else {
                bitmap = Utils.createQRCode(IOS_EWM, 150)
                mBinding.ivQrCode.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_qr_code
    }


}