package com.app.qqwpick.ui.home

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qqwpick.R
import com.app.qqwpick.adapter.ThirdOrderSendListAdapter
import com.app.qqwpick.base.BaseVMFragment
import com.app.qqwpick.data.home.OrderThirdListBean
import com.app.qqwpick.databinding.FragmentOrderSendBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.ActivityUtil
import com.app.qqwpick.util.ORDER_FIRST_INDEX
import com.app.qqwpick.util.ORDER_PAGE_SIZE
import com.app.qqwpick.viewmodels.OrdeSendViewModel
import com.hjq.toast.ToastUtils
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener
import dagger.hilt.android.AndroidEntryPoint

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
                    toMap(bean.receiverAddress, "", "")
                }
                R.id.tv_send -> {
//                    if ((bean.deliveryStartTime?.length ?: 0) == 0) {
//                        viewModel.startDelivery(bean.orderNo)
//                    } else {
//                        showDialog(bean.orderSplitNo)
//                    }
                }
                R.id.tv_call_phone -> {
                    showCallDialog()
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
        viewModel.getThirdBeanList(mCurrentPosition, ORDER_PAGE_SIZE, "", "")
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
            .setTitle("请确认您是否送达")
            .setMessage("")
            .setOkButton("确认送达")
            .setOkButton { baseDialog, v ->
                viewModel.completeDelivery(orderNo)
                false
            }
            .setCancelable(false)
            .setCancelButton("取消")
            .show()
    }

    fun showCallDialog() {
        BottomMenu.show(arrayOf("收货电话", "会员电话", "取消"))
            .setCancelable(false)
            .setOnMenuItemClickListener(object : OnMenuItemClickListener<BottomMenu?> {
                override fun onClick(
                    dialog: BottomMenu?,
                    text: CharSequence?,
                    index: Int
                ): Boolean {
                    ToastUtils.show(text)
                    return false
                }
            })
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_order_send
    }
}