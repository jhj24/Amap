package com.jhj.amapdemo

import android.app.Activity
import com.amap.api.location.AMapLocation
import com.jhj.location.LocationCallback
import com.jhj.prompt.dialog.progress.LoadingFragment
import org.jetbrains.anko.toast

/**
 * Created by jhj on 18-10-20.
 */

fun Activity.getLocation(isCurrentTime: Boolean = false, callBack: (LocationBean?) -> Unit) {

    val position = PreferenceUtil.find(this, "location", LocationBean::class.java)
    val dialog = LoadingFragment.Builder(this)
    dialog.setText("正在获取当前位置")
            .show()

    fun location() {
        val mLocation = (application as MyApplication).location
        mLocation.start()
        mLocation.setLocationListener(object : LocationCallback {
            override fun onLocation(location: AMapLocation) {
                if (location.errorCode == 0) {
                    val locationBean = LocationBean()
                    locationBean.errorCode = location.errorCode
                    locationBean.errorInfo = location.errorInfo
                    locationBean.time = location.time
                    locationBean.latitude = location.latitude
                    locationBean.longitude = location.longitude
                    locationBean.country = location.country
                    locationBean.province = location.province
                    locationBean.city = location.city
                    locationBean.cityCode = location.cityCode
                    locationBean.district = location.district
                    locationBean.address = location.address
                    locationBean.adCode = location.adCode

                    callBack(locationBean)
                    PreferenceUtil.save(this@getLocation, locationBean, "location")
                    mLocation.stop()
                } else if (location.errorCode == 12) {
                    toast("缺少定位权限")
                    /*val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    ActivityCompat.requestPermissions(this@getLocation, permissions, 1000)*/
                } else if (location.errorCode == 4) {
                    toast("网络链接异常")
                } else {
                    toast("定位失败")
                }
                dialog.dismiss()
            }
        })
    }

    if (position != null) {
        val locTime = position.time
        if (System.currentTimeMillis() - locTime <= 5 * 60 * 1000 && !isCurrentTime) {
            callBack(position)
            dialog.dismiss()
        } else {
            location()
        }
    } else {
        location()
    }

}

