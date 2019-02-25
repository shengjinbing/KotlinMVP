package com.example.administrator.kotlinmvp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import com.example.administrator.kotlinmvp.base.BaseActivity
import com.example.administrator.kotlinmvp.mvp.modle.bean.TabEntity
import com.example.administrator.kotlinmvp.ui.fragment.DiscoveryFragment
import com.example.administrator.kotlinmvp.ui.fragment.HomeFragment
import com.example.administrator.kotlinmvp.ui.fragment.HotFragment
import com.example.administrator.kotlinmvp.ui.fragment.MineFragment
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.tencent.bugly.proguard.t
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : BaseActivity() {

    private val mTitles = arrayOf("每日精选", "发现", "热门", "我的")

    private var mHomeFragment: HomeFragment? = null
    private var mDiscoveryFragment: DiscoveryFragment? = null
    private var mHotFragment: HotFragment? = null
    private var mMineFragment: MineFragment? = null

    // 未被选中的图标
    private val mIconUnSelectIds = intArrayOf(R.mipmap.ic_home_normal, R.mipmap.ic_discovery_normal, R.mipmap.ic_hot_normal, R.mipmap.ic_mine_normal)
    // 被选中的图标
    private val mIconSelectIds = intArrayOf(R.mipmap.ic_home_selected, R.mipmap.ic_discovery_selected, R.mipmap.ic_hot_selected, R.mipmap.ic_mine_selected)

    private val mTabEntities = ArrayList<CustomTabEntity>()

    //默认为0
    private var mIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTab();
        tab_layout.currentTab = mIndex
        switchFragment(mIndex)
    }

    private fun initTab() {
        //in,until不包括结束区间
        (0 until mTitles.size)
                .mapTo(mTabEntities) { TabEntity(mTitles[it], mIconSelectIds[it], mIconUnSelectIds[it]) }

        //为Tab赋值
        tab_layout.setTabData(mTabEntities)
        tab_layout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                //切换Fragment
                switchFragment(position)
            }

            override fun onTabReselect(position: Int) {

            }
        })
    }

    override fun initView() {
    }

    override fun initData() {
    }

    override fun star() {
    }

    /**
     * 切换Fragment
     * @param position 下标
     */
    private fun switchFragment(position: Int) {
        val transaction = supportFragmentManager.beginTransaction();
        hideFragments(transaction)
        when (position) {
            0 //首页
            -> if (mHomeFragment == null) {
                mHomeFragment = HomeFragment.getInstance(mTitles[position])
                transaction.add(R.id.fl_container, mHomeFragment, "home")
            } else {
                transaction.show(mHomeFragment)
            }


            1 //发现
            -> if (mDiscoveryFragment == null) {
                mDiscoveryFragment = DiscoveryFragment.getInstance(mTitles[position])
                transaction.add(R.id.fl_container, mDiscoveryFragment, "discovery")
            } else {
                transaction.show(mDiscoveryFragment)
            }
            2 //热门
            -> if (mHotFragment == null) {
                mHotFragment = HotFragment.getInstance(mTitles[position])
                transaction.add(R.id.fl_container, mHotFragment, "hot")
            } else {
                transaction.show(mHotFragment)
            }

            3 //我的
            -> if (mMineFragment == null) {
                mMineFragment = MineFragment.getInstance(mTitles[position])
                transaction.add(R.id.fl_container, mMineFragment, "mine")
            } else {
                transaction.show(mMineFragment)
            }
            else -> {

            }
        }
        mIndex = position
        tab_layout.currentTab = mIndex
        transaction.commitAllowingStateLoss()

    }

    /**
     * 隐藏所有的Fragment
     * @param transaction transaction
     */
    private fun hideFragments(transaction: FragmentTransaction) {
        if (null != mHomeFragment) {
            transaction.hide(mHomeFragment)
        }
        if (null != mDiscoveryFragment) {
            transaction.hide(mDiscoveryFragment)
        }
        if (null != mHotFragment) {
            transaction.hide(mHotFragment)
        }
        if (null != mMineFragment) {
            transaction.hide(mMineFragment)
        }

    }

    override fun layoutId(): Int {
        return R.layout.activity_main;
    }


   /* private var mCurrentTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis().minus(mCurrentTime) <= 2000) {
                finish()
            } else {
                mCurrentTime == System.currentTimeMillis()
                showToast("再点击一次退出")
            }

            return true
        }
        return super.onKeyDown(keyCode, event)
    }*/

}
