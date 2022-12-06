package com.app.qqwpick.ui.home

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.services.geocoder.GeocodeQuery
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeResult
import com.app.qqwpick.R
import com.app.qqwpick.adapter.ThirdOrderSendListAdapter
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.data.home.OrderThirdListBean
import com.app.qqwpick.databinding.ActivityThirdSearchBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.ActivityUtil
import com.app.qqwpick.util.ORDER_FIRST_INDEX
import com.app.qqwpick.util.ORDER_PAGE_SIZE
import com.app.qqwpick.viewmodels.OrdeSendViewModel
import com.hjq.toast.ToastUtils
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback
import com.kongzue.dialogx.interfaces.OnBindView
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import extension.visibleOrGone

@AndroidEntryPoint
class ThirdSearchActivity : BaseVMActivity<ActivityThirdSearchBinding>() {

    private var mPopupWindow: PopupWindow? = null
    var tvSelectSearchType: TextView? = null
    var tvSelectSearchTypeBg: TextView? = null
    private var mCurrentPosition = ORDER_FIRST_INDEX
    private var orderNo = ""
    private var thirdNo = ""
    private val viewModel: OrdeSendViewModel by viewModels()
    private val beanList by lazy {
        mutableListOf<OrderThirdListBean>()
    }

    private val mAdapter by lazy {
        ThirdOrderSendListAdapter(beanList)
    }

    override fun initView(savedInstanceState: Bundle?) {
        tvSelectSearchType = mBinding.tvSelectSearchType
        tvSelectSearchTypeBg = mBinding.tvSelectSearchTypeBg
        mBinding.tvSelectSearchType.setOnClickListener {
            showChangeSelectTypePop()
        }
        mBinding.ivBack.setOnClickListener {
            finish()
        }
        mBinding.common.recyclerData.layoutManager = LinearLayoutManager(this)
        mBinding.common.recyclerData.adapter = mAdapter

        mBinding.common.refreshLayout.setOnRefreshListener {
            mCurrentPosition = ORDER_FIRST_INDEX
            getData()
        }
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            mCurrentPosition++
            getData()
        }
        mAdapter.setOnItemClickListener { adapter, view, position ->
            var intent = Intent(this, ThirdDetailActivity::class.java)
            intent.putExtra("orderNo", mAdapter.getItem(position).orderNo)
            startActivity(intent)
        }
        mAdapter.addChildClickViewIds(R.id.tv_receive_address, R.id.tv_send, R.id.tv_call_phone)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            var bean = mAdapter.getItem(position)
            when (view.id) {
                R.id.tv_receive_address -> {
                    toMap(bean.receiverAddress)
                }
                R.id.tv_send -> {
                    if (bean.deliveryInfo.currentStatus == 4) {
                        showDialog(bean.orderNo)
                    } else {
                        viewModel.startThirdDelivery(bean.orderNo)
                    }
                }
                R.id.tv_call_phone -> {
                    showCallDialog(bean)
                }
            }
        }
        mBinding.editSearchActivityContent.setOnEditorActionListener(TextView.OnEditorActionListener { textView: TextView?, actionId: Int, keyEvent: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (mBinding.editSearchActivityContent.text.isNullOrEmpty()) {
                    ToastUtils.show("请输入要搜索的内容")
                } else {
                    getData()
                }
                hideSoftInput(mBinding.editSearchActivityContent.windowToken)
                true
            }
            false
        })
    }

    private fun getData() {
        mAdapter.loadMoreModule.isEnableLoadMore = false
        if (!orderNo.isNullOrEmpty()) {
            viewModel.getThirdBeanList(mCurrentPosition, ORDER_PAGE_SIZE, orderNo, "")
        } else if (!thirdNo.isNullOrEmpty()) {
            viewModel.getThirdBeanList(mCurrentPosition, ORDER_PAGE_SIZE, "", thirdNo)
        } else {
            var ss = mBinding.editSearchActivityContent.text.toString()
            viewModel.getThirdBeanList(mCurrentPosition, ORDER_PAGE_SIZE, ss, "")
        }
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.thirdBeanList.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_LOADING -> {
                    if (mCurrentPosition == ORDER_FIRST_INDEX && !mBinding.common.refreshLayout.isRefreshing) {
                        showLoading()
                    }
                }
                DataStatus.STATE_SUCCESS -> {
                    finishRefresh()
                    mAdapter.loadMoreModule.isEnableLoadMore = true
                    if (mCurrentPosition == ORDER_FIRST_INDEX) {
                        beanList.clear()
                        mAdapter.notifyDataSetChanged()
                        if (it.data?.list.isNullOrEmpty()) {
                            //如果网络错误了
                            mAdapter.setEmptyView(
                                getMsgEmptyDataView(
                                    mBinding.common.recyclerData
                                )
                            )
                            return@observe
                        }
                    }
                    beanList.addAll(it.data?.list!!)
                    mAdapter.notifyDataSetChanged()
                    //判断是否是最后一页
                    if (it.data?.list?.size ?: 0 < ORDER_PAGE_SIZE) {
                        mAdapter.loadMoreModule.loadMoreEnd()
                    } else {
                        mAdapter.loadMoreModule.loadMoreComplete()
                    }
                }
                DataStatus.STATE_ERROR -> {
                    finishRefresh()
                    if (mCurrentPosition == ORDER_FIRST_INDEX) {
                        beanList.clear()
                        mAdapter.notifyDataSetChanged()
                        //如果网络错误了
                        mAdapter.setEmptyView(
                            getMsgErrorView(
                                mBinding.common.recyclerData,
                                it.exception?.msg
                            )
                        )
                    }
                }
            }
        })

        viewModel.stratThirdDelivery.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_LOADING -> {
                    showLoading()
                }
                DataStatus.STATE_SUCCESS -> {
                    toast("操作成功")
                    getData()
                }
                DataStatus.STATE_ERROR -> {
                    dismissLoading()
                    toast(it.exception!!.msg)
                }
            }
        })

        viewModel.finishThirdDelivery.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_LOADING -> {
                    showLoading()
                }
                DataStatus.STATE_SUCCESS -> {
                    toast("操作成功")
                    getData()
                }
                DataStatus.STATE_ERROR -> {
                    dismissLoading()
                    toast(it.exception!!.msg)
                }
            }
        })
    }

    private fun finishRefresh() {
        if (mCurrentPosition == ORDER_FIRST_INDEX) {
            if (!mBinding.common.refreshLayout.isRefreshing) {
                dismissLoading()
            } else {
                mBinding.common.refreshLayout.finishRefresh()
            }
        }
    }

    private fun toMap(address: String) {
        val geocodeSearch = GeocodeSearch(this)
        geocodeSearch.setOnGeocodeSearchListener(object : GeocodeSearch.OnGeocodeSearchListener {
            override fun onRegeocodeSearched(regeocodeResult: RegeocodeResult, i: Int) {}
            override fun onGeocodeSearched(geocodeResult: GeocodeResult, i: Int) {
                if (i == 1000) {
                    if (geocodeResult != null && geocodeResult.geocodeAddressList != null && geocodeResult.geocodeAddressList.size > 0
                    ) {
                        val geocodeAddress =
                            geocodeResult.geocodeAddressList[0]
                        val latitude =
                            geocodeAddress.latLonPoint.latitude //纬度
                        val longititude =
                            geocodeAddress.latLonPoint.longitude //经度
                        if (ActivityUtil.isInstallApk("com.autonavi.minimap")) {
                            val intent = Intent.getIntent(
                                "androidamap://navi?sourceApplication=&poiname=" + address + "&lat=" + latitude
                                        + "&lon=" + longititude + "&dev=0"
                            )
                            startActivity(intent)
                        } else {
                            ToastUtils.show("没有安装高德地图")
                        }
                    } else {
                        ToastUtils.show("地址名出错");
                    }
                }
            }
        })
        val geocodeQuery = GeocodeQuery(address, "29")
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery)
    }

    /**
     * 切换搜索类型，订单号或者三方单号
     */
    private fun showChangeSelectTypePop() {
        if (mPopupWindow == null) {
            val view = View.inflate(this, R.layout.pop_select_search_type, null)
            mPopupWindow = PopupWindow(this)
            mPopupWindow!!.width = ConstraintLayout.LayoutParams.MATCH_PARENT
            mPopupWindow!!.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            mPopupWindow!!.contentView = view
            mPopupWindow!!.isFocusable = true
            mPopupWindow!!.isOutsideTouchable = true
            mPopupWindow!!.update()
            val dw = ColorDrawable(0)
            mPopupWindow!!.setBackgroundDrawable(dw)
            val tvPhone = view.findViewById<TextView>(R.id.tv_phone)
            val tvOrder = view.findViewById<TextView>(R.id.tv_order)
            tvPhone.text = "订单号"
            tvOrder.text = "三方单号"
            if (tvSelectSearchType?.getText() == tvPhone.text) {
                tvPhone.isSelected = true
                tvOrder.isSelected = false
            } else {
                tvPhone.isSelected = false
                tvOrder.isSelected = true
            }
            tvPhone.setOnClickListener {
                if (mPopupWindow != null) {
                    mPopupWindow!!.dismiss()
                    mPopupWindow = null
                }
                tvSelectSearchType!!.text = "订单号"
                mBinding.editSearchActivityContent.hint = "请输入订单号进行搜索"
                orderNo = mBinding.editSearchActivityContent.text.toString()
                thirdNo = ""
            }
            tvOrder.setOnClickListener {
                if (mPopupWindow != null) {
                    mPopupWindow!!.dismiss()
                    mPopupWindow = null
                }
                tvSelectSearchType!!.text = "三方单号"
                mBinding.editSearchActivityContent.hint = "请输入三方单号进行搜索"
                orderNo = ""
                thirdNo = mBinding.editSearchActivityContent.text.toString()
            }
            mPopupWindow!!.width = tvSelectSearchType?.getWidth()!!
        }
        if (mPopupWindow!!.isShowing) {
            mPopupWindow!!.dismiss()
        } else {
            mPopupWindow!!.setOnDismissListener { goneBg() }
            mPopupWindow!!.showAsDropDown(tvSelectSearchType)
            showBg()
        }
    }

    private fun showBg() {
        tvSelectSearchTypeBg?.visibleOrGone(true)
        val drawableRight = resources.getDrawable(
            R.mipmap.icon_close_search
        )
        drawableRight.setBounds(
            0,
            0,
            drawableRight.minimumWidth,
            drawableRight.minimumHeight
        ) //设置边界
        tvSelectSearchType!!.setCompoundDrawables(null, null, drawableRight, null)
    }

    private fun goneBg() {
        tvSelectSearchTypeBg?.visibleOrGone(false)
        val drawableRight = resources.getDrawable(
            R.mipmap.icon_open_search
        )
        drawableRight.setBounds(
            0,
            0,
            drawableRight.minimumWidth,
            drawableRight.minimumHeight
        ) //设置边界
        tvSelectSearchType!!.setCompoundDrawables(null, null, drawableRight, null)
    }

    fun showDialog(orderNo: String) {
        MessageDialog.build()
            .setCancelable(false)
            .setCustomView(object : OnBindView<MessageDialog?>(R.layout.sure_order) {
                override fun onBind(dialog: MessageDialog?, v: View) {
                    var tvCancel = v.findViewById<TextView>(R.id.tv_cancel)
                    tvCancel.setOnClickListener {
                        dialog?.dismiss()
                    }
                    var tvSure = v.findViewById<TextView>(R.id.tv_sure)
                    tvSure.setOnClickListener {
                        dialog?.dismiss()
                        viewModel.finishThirdDelivery(orderNo)
                    }
                }
            }).setMaskColor(getResources().getColor(R.color.black30))
            .setDialogLifecycleCallback(object : DialogLifecycleCallback<MessageDialog>() {
                override fun onShow(dialog: MessageDialog?) {
                    super.onShow(dialog)
                    dialog?.getDialogImpl()?.bkg?.setBackgroundResource(R.drawable.round_conner_white_bg_10)
                }
            }).show()
    }

    fun showCallDialog(bean: OrderThirdListBean) {
        BottomMenu.show(arrayOf("收货电话", "取消"))
            .setCancelable(false)
            .setOnMenuItemClickListener(object : OnMenuItemClickListener<BottomMenu?> {
                override fun onClick(
                    dialog: BottomMenu?,
                    text: CharSequence?,
                    index: Int
                ): Boolean {
                    if (text?.equals("收货电话") == true) {
                        var intent = Intent()
                        intent.action = Intent.ACTION_DIAL//dial是拨号的意思
                        intent.data =
                            Uri.parse("tel:" + bean.receiverPrivacyPhone)//这个tel不能改，后面的数字可以随便改
                        startActivity(intent)
                    }
                    return false
                }
            })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_third_search
    }
}