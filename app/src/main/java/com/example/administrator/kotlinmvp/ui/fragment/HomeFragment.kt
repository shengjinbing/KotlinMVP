package com.example.administrator.kotlinmvp.ui.fragment


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.administrator.kotlinmvp.R
import com.example.administrator.kotlinmvp.base.BaseFragment
import com.example.administrator.kotlinmvp.mvp.contract.HomeContract
import com.example.administrator.kotlinmvp.mvp.modle.bean.HomeBean
import com.example.administrator.kotlinmvp.mvp.presenter.HomePresenter
import com.example.administrator.kotlinmvp.showToast
import com.example.administrator.kotlinmvp.ui.activity.SearchActivity
import com.example.administrator.kotlinmvp.ui.adapter.HomeAdapter
import com.example.administrator.kotlinmvp.utils.StatusBarUtil
import com.hazz.kotlinmvp.net.exception.ErrorStatus
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.header.MaterialHeader
import com.tencent.bugly.proguard.t
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.transform.Templates


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : BaseFragment(), HomeContract.View {

    private var mTilte: String? = null;

    private var mMaterialHeader: MaterialHeader? = null

    private var isRefresh = false

    private var loadingMore = false

    private var mHomeAdapter: HomeAdapter? = null

    private var num: Int = 1

    private val mPersent: HomePresenter by lazy {
        HomePresenter()
    }

    private val linearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }


    private val simpleDateFormat by lazy {
        SimpleDateFormat("- MMM. dd, 'Brunch' -", Locale.ENGLISH)
    }

    companion object {
        fun getInstance(title: String): HomeFragment {
            val fragment = HomeFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTilte = title
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home;
    }

    override fun initView() {
        mPersent.attachView(this)

        mRefreshLayout.setOnRefreshListener({
            isRefresh = true
            mPersent.run {
                requestHomeData(num)
            }
        }
        )

        //内容跟随偏移
        mRefreshLayout.setEnableHeaderTranslationContent(true)

        mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
        //打开下拉刷新区域块背景:
        mMaterialHeader?.setShowBezierWave(true)
        //设置下拉刷新主题颜色
        mRefreshLayout.setPrimaryColorsId(R.color.color_light_black, R.color.color_title_bg)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                if (currentVisibleItemPosition == 0) {
                    //背景设置为透明
                    toolbar.setBackgroundColor(getColor(R.color.color_translucent))
                    iv_search.setImageResource(R.mipmap.ic_action_search_white)
                    tv_header_title.text = ""
                } else {
                    if (mHomeAdapter?.mData!!.size > 1) {
                        toolbar.setBackgroundColor(getColor(R.color.color_title_bg))
                        iv_search.setImageResource(R.mipmap.ic_action_search_black)
                        val itemList = mHomeAdapter!!.mData
                        val item = itemList[currentVisibleItemPosition + mHomeAdapter!!.bannerItemSize - 1]
                        if (item.type == "textHeader") {
                            tv_header_title.text = item.data?.text
                        } else {
                            tv_header_title.text = simpleDateFormat.format(item.data?.date)
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val childCount = mRecyclerView.childCount;
                val itemCount = mRecyclerView.layoutManager.itemCount
                val firstVisibleItem = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
Log.d("BBBBB","das"+childCount+","+itemCount+","+firstVisibleItem)
                when(newState){
                    RecyclerView.SCROLL_STATE_IDLE ->
                        if (firstVisibleItem + childCount == itemCount) {
                            if (!loadingMore) {
                                loadingMore = true
                                mPersent.LoadMoreData()
                            }
                        }
                }
            }
        })

        iv_search.setOnClickListener { openSearchActivity() }

        mLayoutStatusView = multipleStatusView

        //状态栏透明和间距处理
        StatusBarUtil.darkMode(activity)
        StatusBarUtil.setPaddingSmart(activity, toolbar)
    }

    private fun openSearchActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, iv_search, iv_search.transitionName)
            startActivity(Intent(activity, SearchActivity::class.java), options.toBundle())
        } else {
            startActivity(Intent(activity, SearchActivity::class.java))
        }
    }

    override fun lazyLoad() {
        mPersent.requestHomeData(num)
    }


    override fun showLoading() {
        if (!isRefresh) {
            isRefresh = false
            mLayoutStatusView?.showLoading()
        }    }

    override fun dismissLoading() {
        mRefreshLayout.finishRefresh()
        mLayoutStatusView?.showContent()
    }

    override fun setHomeData(homeBean: HomeBean) {
        Logger.d(homeBean)

        // Adapter
        mHomeAdapter = HomeAdapter(activity, homeBean.issueList[0].itemList)
        //设置 banner 大小
        mHomeAdapter?.setBannerSize(homeBean.issueList[0].count)

        mRecyclerView.adapter = mHomeAdapter
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()    }

    override fun setMoreData(itemList: ArrayList<HomeBean.Issue.Item>) {
        loadingMore = false
        mHomeAdapter?.addItemData(itemList)    }

    override fun showError(msg: String, errorCode: Int) {
        showToast(msg)
        if (errorCode == ErrorStatus.NETWORK_ERROR) {
            mLayoutStatusView?.showNoNetwork()
        } else {
            mLayoutStatusView?.showError()
        }    }

    override fun onDestroy() {
        super.onDestroy()
        mPersent.detachView()
    }


    fun getColor(colorId: Int): Int {
        return resources.getColor(colorId)
    }
}
