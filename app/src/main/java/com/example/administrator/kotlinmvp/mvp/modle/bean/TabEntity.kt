package com.example.administrator.kotlinmvp.mvp.modle.bean

import com.flyco.tablayout.listener.CustomTabEntity

/**
 * Created by Administrator on 2017/12/27 0027.
 */
class TabEntity(var title: String, private var selectedIcon: Int, private var unSelectedIcon: Int) : CustomTabEntity {

    override fun getTabTitle(): String {
        return title
    }

    override fun getTabSelectedIcon(): Int {
        return selectedIcon
    }

    override fun getTabUnselectedIcon(): Int {
        return unSelectedIcon
    }

    fun sort(data: IntArray) {
        var temp = 0
        for (i in data.indices) {
            for (j in 0 until data.size - i - 1) {
                if (data[j] > data[j + 1]) {
                    temp = data[j]
                    data[j] = data[j + 1]
                    data[j + 1] = temp
                }
            }
        }
    }
}