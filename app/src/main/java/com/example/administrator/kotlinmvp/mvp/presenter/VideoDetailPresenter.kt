package com.example.administrator.kotlinmvp.mvp.presenter

import android.app.Activity
import com.example.administrator.kotlinmvp.base.BasePresenter
import com.example.administrator.kotlinmvp.base.MyApplication
import com.example.administrator.kotlinmvp.dataFormat
import com.example.administrator.kotlinmvp.mvp.contract.VideoDetailContract
import com.example.administrator.kotlinmvp.mvp.modle.VideoDetailModle
import com.example.administrator.kotlinmvp.mvp.modle.bean.HomeBean
import com.example.administrator.kotlinmvp.showToast
import com.example.administrator.kotlinmvp.utils.DisplayManager
import com.example.administrator.kotlinmvp.utils.NetworkUtil
import com.example.administrator.kotlinmvp.utils.RetrofitManage
import com.hazz.kotlinmvp.net.exception.ExceptionHandle

/**
 * Created by Administrator on 2018/3/27 0027.
 */
class VideoDetailPresenter : BasePresenter<VideoDetailContract.View>(), VideoDetailContract.Presenter {

    val mVideoDetailModle : VideoDetailModle by lazy { VideoDetailModle() }



    /**
     * 加载视频相关的数据
     */
    override fun loadVideoInfo(itemInfo: HomeBean.Issue.Item) {

        val playInfo = itemInfo.data?.playInfo

        val netType = NetworkUtil.isWifi(MyApplication.context)
        // 检测是否绑定 View
        checkViewAttached()
        if (playInfo!!.size > 1) {
            // 当前网络是 Wifi环境下选择高清的视频
            if (netType) {
                for (i in playInfo) {
                    if (i.type == "high") {
                        val playUrl = i.url
                        mRootView?.setVideo(playUrl)
                        break
                    }
                }
            } else {
                //否则就选标清的视频
                for (i in playInfo) {
                    if (i.type == "normal") {
                        val playUrl = i.url
                        mRootView?.setVideo(playUrl)
                        //Todo 待完善
                        (mRootView as Activity).showToast("本次消耗${(mRootView as Activity)
                                .dataFormat(i.urlList[0].size)}流量")
                        break
                    }
                }
            }
        } else {
            mRootView?.setVideo(itemInfo.data.playUrl)
        }

        //设置背景
        val backgroundUrl = itemInfo.data.cover.blurred + "/thumbnail/${DisplayManager.getScreenHeight()!! - DisplayManager.dip2px(250f)!!}x${DisplayManager.getScreenWidth()}"
        backgroundUrl.let { mRootView?.setBackground(it) }

        mRootView?.setVideoInfo(itemInfo)


    }


    /**
     * 请求相关的视频数据
     */
    override fun requestRelatedVideo(id: Long) {
        mRootView?.showLoading()
        val disposable = mVideoDetailModle.requestRelatedData(id)
                .subscribe({ issue ->
                    mRootView?.apply {
            dismissLoading()
            setRecentRelatedVideo(issue.itemList)
        }
    }, { t ->
        mRootView?.apply {
            dismissLoading()
            setErrorMsg(ExceptionHandle.handleException(t))
        }
    })

    addSubscription(disposable)

    }

}