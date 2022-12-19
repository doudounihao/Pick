package com.app.qqwpick.ui.home

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.services.geocoder.GeocodeQuery
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener
import com.amap.api.services.geocoder.RegeocodeResult
import com.app.qqwpick.R
import com.app.qqwpick.adapter.ThirdOrderSendListAdapter
import com.app.qqwpick.base.BaseVMFragment
import com.app.qqwpick.data.home.MapBean
import com.app.qqwpick.data.home.OrderThirdListBean
import com.app.qqwpick.databinding.FragmentOrderSendBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.*
import com.app.qqwpick.viewmodels.OrdeSendViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.toast.ToastUtils
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback
import com.kongzue.dialogx.interfaces.OnBindView
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ThirdOrderSendFragment : BaseVMFragment<FragmentOrderSendBinding>() {

    private var mCurrentPosition = ORDER_FIRST_INDEX
    private val viewModel: OrdeSendViewModel by viewModels()
    private val beanList by lazy {
        mutableListOf<OrderThirdListBean>()
    }

    private val mAdapter by lazy {
        ThirdOrderSendListAdapter(beanList)
    }

    override fun onResume() {
        super.onResume()
        (parentFragment as ThirdFragment).setType(3)
        getData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            getData()
        }
    }

    override fun initView(view: View) {
        mBinding.common.recyclerData.layoutManager = LinearLayoutManager(requireContext())
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
            var intent = Intent(requireContext(), ThirdDetailActivity::class.java)
            intent.putExtra("orderNo", mAdapter.getItem(position).orderNo)
            startActivity(intent)
        }
        mAdapter.addChildClickViewIds(R.id.tv_receive_address, R.id.tv_send, R.id.tv_call_phone)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            var bean = mAdapter.getItem(position)
            when (view.id) {
                R.id.tv_receive_address -> {
                    toMap(bean)
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
    }

    private fun getData() {
        mAdapter.loadMoreModule.isEnableLoadMore = false
        viewModel.getThirdBeanList(
            mCurrentPosition,
            ORDER_PAGE_SIZE,
            "",
            THIRD_SEND_STATUS,
            "",
            "",
            ""
        )
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
                            EventBus.getDefault()
                                .postSticky(
                                    MessageEvent(MessageType.thirdOrderBean).putThird(
                                        beanList
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
                    EventBus.getDefault()
                        .postSticky(MessageEvent(MessageType.thirdOrderBean).putThird(beanList))
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
                        EventBus.getDefault()
                            .postSticky(MessageEvent(MessageType.thirdOrderBean).putThird(beanList))
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

    private fun toMap(bean: OrderThirdListBean) {
        var address =
            bean.receiverCity + bean.receiverProvince + bean.receiverDistrict + bean.receiverAddress
        if (!bean.receiverCoords.isNullOrEmpty()) {
            var maoList: ArrayList<MapBean>
            var listType = object : TypeToken<ArrayList<MapBean>>() {}.type
            maoList = Gson().fromJson(bean.receiverCoords, listType)
            var dataList = maoList.filter {
                it.coordsType.equals("1")
            }
            if (dataList.size > 0) {
                toGaoDe(address, maoList.get(0).lat, maoList.get(0).lng)
            } else {
                searchGaoDe(address)
            }
        } else {
            searchGaoDe(address)
        }
    }

    private fun searchGaoDe(address: String) {
        val geocodeSearch = GeocodeSearch(context)
        geocodeSearch.setOnGeocodeSearchListener(object : OnGeocodeSearchListener {
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
                        toGaoDe(address, latitude, longititude)
                    } else {
                        ToastUtils.show("地址名出错");
                    }
                }
            }
        })
        val geocodeQuery = GeocodeQuery(address, "29")
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery)
    }

    private fun toGaoDe(address: String, latitude: Double, longititude: Double) {
        if (ActivityUtil.isInstallApk("com.autonavi.minimap")) {
            val intent = Intent.getIntent(
                "androidamap://navi?sourceApplication=&poiname=" + address + "&lat=" + latitude
                        + "&lon=" + longititude + "&dev=0"
            )
            startActivity(intent)
        } else {
            ToastUtils.show("没有安装高德地图")
        }
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
        return R.layout.fragment_order_send
    }

    //接收消息
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: MessageEvent) {
        when (event.type) {
            MessageType.thirdListRefresh -> {
                getData()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)//注册，重复注册会导致崩溃
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)//解绑
    }
}