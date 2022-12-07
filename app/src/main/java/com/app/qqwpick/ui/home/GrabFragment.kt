package com.app.qqwpick.ui.home

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qqwpick.R
import com.app.qqwpick.adapter.GrabAdapter
import com.app.qqwpick.base.BaseVMFragment
import com.app.qqwpick.data.home.GrabDetailBean
import com.app.qqwpick.data.home.GrabListBean
import com.app.qqwpick.databinding.FragmentGrabBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.*
import com.app.qqwpick.viewmodels.GrabViewModel
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindView
import dagger.hilt.android.AndroidEntryPoint
import extension.visibleOrGone
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@AndroidEntryPoint
class GrabFragment : BaseVMFragment<FragmentGrabBinding>() {

    private var mCurrentPosition = ORDER_FIRST_INDEX

    private val viewModel: GrabViewModel by viewModels()
    private val beanList by lazy {
        mutableListOf<GrabListBean>()
    }
    private val mAdapter by lazy {
        GrabAdapter(beanList)
    }

    override fun initView(view: View) {
        mBinding.tvStoreName.text = SpUtils.getString(STORE_NAME)
        mBinding.recyclerData.layoutManager = LinearLayoutManager(requireContext())
        mBinding.recyclerData.adapter = mAdapter

        mBinding.refreshLayout.setOnRefreshListener {
            getData()
        }

        mAdapter.addChildClickViewIds(R.id.tv_grab)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            var bean = mAdapter.getItem(position)
            when (view.id) {
                R.id.tv_grab -> {
                    viewModel.grabOrder(bean.orderNo)
                }
            }
        }

        mBinding.ivTopBg.setOnClickListener {
            if (mBinding.tvNum.text.toString().toInt() > 0) {
                viewModel.grabOrder("")
            }
        }
    }

    override fun initData() {
        super.initData()
        getData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            getData()
        }
    }

    fun getData() {
        mAdapter.loadMoreModule.isEnableLoadMore = false
        viewModel.getGrabList(mCurrentPosition, ORDER_PAGE_SIZE)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)//注册，重复注册会导致崩溃
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)//解绑
    }

    //接收消息
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: MessageEvent) {
        when (event.type) {
            MessageType.grabRefresh -> {
                if (event.getInt() > 0 || event.getInt() == 0 && beanList.size > 0) {
                    getData()
                }
            }
        }
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.grabBeanList.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_LOADING -> {
                    if (mCurrentPosition == ORDER_FIRST_INDEX && !mBinding.refreshLayout.isRefreshing) {
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
                                    mBinding.recyclerData
                                )
                            )
                            mBinding.tvNum.text = "0"
                            EventBus.getDefault()
                                .postSticky(MessageEvent(MessageType.ShowGrab).put(0))
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
                    mBinding.tvNum.text = beanList.size.toString()
                    EventBus.getDefault()
                        .postSticky(MessageEvent(MessageType.ShowGrab).put(beanList.size))
                }
                DataStatus.STATE_ERROR -> {
                    finishRefresh()
                    mBinding.tvNum.text = "0"
                    EventBus.getDefault()
                        .postSticky(MessageEvent(MessageType.ShowGrab).put(0))
                    if (mCurrentPosition == ORDER_FIRST_INDEX) {
                        beanList.clear()
                        mAdapter.notifyDataSetChanged()
                        //如果网络错误了
                        mAdapter.setEmptyView(
                            getMsgErrorView(
                                mBinding.recyclerData,
                                it.exception?.msg
                            )
                        )
                    }
                }
            }
        })

        viewModel.grabBean.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_ERROR -> {
                    toast(it.exception!!.msg)
                }
                DataStatus.STATE_SUCCESS -> {
                    setGrabDialog(it.data)
                }
            }
        })
    }

    fun setGrabDialog(grabBean: GrabDetailBean?) {
        MessageDialog.show("", "", "", "")
            .setCancelable(false)
            .setCustomView(object : OnBindView<MessageDialog?>(R.layout.pop_grab) {
                override fun onBind(dialog: MessageDialog?, v: View) {
                    var childView = v.findViewById<TextView>(R.id.tv_num)
                    childView.text = grabBean?.serialNumber
                    var tvAddress = v.findViewById<TextView>(R.id.tv_address)
                    tvAddress.text = grabBean?.receiverAddress
                    var tvStroeName = v.findViewById<TextView>(R.id.tv_store_name)
                    tvStroeName.text = grabBean?.storeName
                    var tvTime = v.findViewById<TextView>(R.id.tv_create_time)
                    tvTime.text = DateUtils.getDateTimeMdsf(grabBean?.bespokeTimeTo) + "送达"
                    var isTime = v.findViewById<TextView>(R.id.tv_type)
                    if (grabBean?.isTimelyOrder == true) {
                        isTime.visibleOrGone(true)
                    } else {
                        isTime.visibleOrGone(false)
                    }
                    var tvSure = v.findViewById<TextView>(R.id.tv_sure)
                    tvSure.setOnClickListener {
                        dialog?.dismiss()
                        getData()
                    }
                }
            })
    }

    private fun finishRefresh() {
        if (mCurrentPosition == ORDER_FIRST_INDEX) {
            if (!mBinding.refreshLayout.isRefreshing) {
                dismissLoading()
            } else {
                mBinding.refreshLayout.finishRefresh()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_grab
    }
}