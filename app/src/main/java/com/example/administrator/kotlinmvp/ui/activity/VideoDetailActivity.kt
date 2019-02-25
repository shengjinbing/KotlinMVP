package com.example.administrator.kotlinmvp.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.administrator.kotlinmvp.Constants
import com.example.administrator.kotlinmvp.R
import com.example.administrator.kotlinmvp.base.BaseActivity
import com.example.administrator.kotlinmvp.mvp.contract.VideoDetailContract
import com.example.administrator.kotlinmvp.mvp.modle.bean.HomeBean
import com.example.administrator.kotlinmvp.mvp.presenter.VideoDetailPresenter
import com.example.administrator.kotlinmvp.ui.adapter.VideoDetailAdapter
import com.example.administrator.kotlinmvp.utils.StatusBarUtil
import com.scwang.smartrefresh.header.MaterialHeader
import kotlinx.android.synthetic.main.activity_video_detail.*

class VideoDetailActivity : BaseActivity() ,VideoDetailContract.View{

    val itemdata  = ArrayList<HomeBean.Issue.Item>();

    /**
     * Item 详细数据
     */
    private lateinit var itemData: HomeBean.Issue.Item

    val mPresenter : VideoDetailPresenter by lazy { VideoDetailPresenter() }

    val mAdapter : VideoDetailAdapter by  lazy { VideoDetailAdapter(this,itemdata)  }
    private var isTransition: Boolean = false


    companion object {
        val IMG_TRANSITION = "IMG_TRANSITION"
        val TRANSITION = "TRANSITION"
    }

    override fun layoutId(): Int {
        return R.layout.activity_video_detail
    }

    override fun initView() {
        mPresenter.attachView(this)
        mAdapter.setOnItemDetailClick { mPresenter.loadVideoInfo(it) }


        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter


        //状态栏透明和间距处理
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, mVideoView)

        /***  下拉刷新  ***/
        //内容跟随偏移
        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener{
            loadVideoInfo()
        }

        val header = mRefreshLayout.refreshHeader as MaterialHeader;
        //打开下拉刷新区域块背景:
        header.setShowBezierWave(true)
        //设置下拉刷新主题颜色
        mRefreshLayout.setPrimaryColors(R.color.color_light_black, R.color.color_title_bg)
    }


    /**
     * 1.加载视频信息
     */
    fun loadVideoInfo() {
        mPresenter.loadVideoInfo(itemData)
    }


    override fun initData() {
        itemData = intent.getSerializableExtra(Constants.BUNDLE_VIDEO_DATA) as HomeBean.Issue.Item
        isTransition = intent.getBooleanExtra(TRANSITION, false)

        saveWatchVideoHistoryInfo(itemData)
    }


    /**
     * 观看保存记录
     */
    private fun saveWatchVideoHistoryInfo(itemData: HomeBean.Issue.Item) {

    }

    override fun star() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dismissLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVideo(url: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVideoInfo(itemInfo: HomeBean.Issue.Item) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setBackground(url: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setRecentRelatedVideo(itemList: ArrayList<HomeBean.Issue.Item>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setErrorMsg(errorMsg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
