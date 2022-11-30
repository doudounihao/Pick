package com.app.qqwpick.ui.home

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

    fun getData() {
        mAdapter.loadMoreModule.isEnableLoadMore = false
        viewModel.getGrabList(mCurrentPosition, ORDER_PAGE_SIZE)
    }

    override fun startObserver() {
        super.startObserver()

//        viewModel.grabNum.observe(this, {
//            when (it.dataStatus) {
//                DataStatus.STATE_ERROR -> {
//                    toast(it.exception!!.msg)
//                }
//                DataStatus.STATE_SUCCESS -> {
//                    mBinding.tvNum.text = it.data.toString()
//                    if (it.data!! > 0) {
//                        getData()
//                    }
//                }
//            }
//        })

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
                        if (it.data?.list.isNullOrEmpty()) {
                            //必须要先把数组设置为空
                            mAdapter.setNewInstance(mutableListOf())
                            //如果网络错误了
                            mAdapter.setEmptyView(
                                getMsgEmptyDataView(
                                    mBinding.recyclerData
                                )
                            )
                            mBinding.tvNum.text = "0"
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
                    mBinding.tvNum.text = beanList.size.toString()
                }
                DataStatus.STATE_ERROR -> {
                    finishRefresh()
                    mBinding.tvNum.text = "0"
                    if (mCurrentPosition == ORDER_FIRST_INDEX) {
                        //必须要先把数组设置为空
                        mAdapter.setNewInstance(mutableListOf())
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