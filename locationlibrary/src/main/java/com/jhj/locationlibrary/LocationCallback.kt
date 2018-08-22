package com.jhj.locationlibrary

import com.amap.api.location.AMapLocation

/**
 * 定位
 * Created by jhj on 18-8-22.
 */

interface LocationCallback {
    fun onLocation(location: AMapLocation)
}
