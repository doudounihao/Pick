package com.app.qqwpick.ui.home

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qqwpick.R
import com.app.qqwpick.adapter.OrderSendListAdapter
import com.app.qqwpick.base.BaseVMFragment
import com.app.qqwpick.data.home.OrderListBean
import com.app.qqwpick.databinding.FragmentOrderSendBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.*
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
class OrderSendFragment : BaseVMFragment<FragmentOrderSendBinding>() {

    private var mCurrentPosition = ORDER_FIRST_INDEX
    private var orderNo = ""
    private val viewModel: OrdeSendViewModel by viewModels()
    private val beanList by lazy {
        mutableListOf<OrderListBean>()
    }

    private val mAdapter by lazy {
        OrderSendListAdapter(beanList)
    }

    override fun onResume() {
        super.onResume()
        (parentFragment as OrderFragment).setType(1)
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
            var intent = Intent(requireContext(), OrderDetailActivity::class.java)
            intent.putExtra("bean", mAdapter.getItem(position))
            startActivity(intent)
        }
        mAdapter.addChildClickViewIds(R.id.tv_receive_address, R.id.tv_send, R.id.tv_call_phone)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            var bean = mAdapter.getItem(position)
            when (view.id) {
                R.id.tv_receive_address -> {
                    toMap(bean.receiverAddress, bean.lat, bean.lng)
                }
                R.id.tv_send -> {
                    if ((bean.deliveryStartTime?.length ?: 0) == 0) {
                        viewModel.startDelivery(bean.orderNo)
                    } else {
                        showDialog(bean.orderSplitNo)
                    }
                }
                R.id.tv_call_phone -> {
                    showCallDialog(bean)
                }
            }
        }
    }

    override fun initData() {
        super.initData()
        getData()
    }

    private fun getData() {
        mAdapter.loadMoreModule.isEnableLoadMore = false
        viewModel.getBeanList(mCurrentPosition, ORDER_PAGE_SIZE, orderNo, SEND_STATUS)
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.beanList.observe(this, {
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
                        if (it.data?.list.isNullOrEmpty()) {
                            //必须要先把数组设置为空
                            mAdapter.setNewInstance(mutableListOf())
                            //如果网络错误了
                            mAdapter.setEmptyView(
                                getMsgEmptyDataView(
                                    mBinding.common.recyclerData
                                )
                            )
                            return@observe
                        }
                        beanList.clear()
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
                        //必须要先把数组设置为空
                        mAdapter.setNewInstance(mutableListOf())
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

        viewModel.startResult.observe(this, {
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

        viewModel.completeResult.observe(this, {
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

    private fun toMap(address: String, latitude: String, longtitude: String) {
        if (ActivityUtil.isInstallApk("com.autonavi.minimap")) {
            val intent = Intent.getIntent(
                "androidamap://navi?sourceApplication=&poiname=" + address + "&lat=" + latitude
                        + "&lon=" + longtitude + "&dev=0"
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
                        viewModel.completeDelivery(orderNo)
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

    fun showCallDialog(bean: OrderListBean) {
        BottomMenu.show(arrayOf("收货电话", "会员电话", "取消"))
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
                        intent.data = Uri.parse("tel:" + bean.receiverMobile)//这个tel不能改，后面的数字可以随便改
                        startActivity(intent)
                    } else if (text?.equals("会员电话") == true) {
                        var intent = Intent()
                        intent.action = Intent.ACTION_DIAL//dial是拨号的意思
                        intent.data = Uri.parse("tel:" + bean.phone)//这个tel不能改，后面的数字可以随便改
                        startActivity(intent)
                    }
                    return false
                }
            })
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_order_send
    }
}