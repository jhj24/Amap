package com.jhj.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.AMapOptions
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.RegeocodeAddress
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import kotlinx.android.synthetic.main.activity_amap.*
import java.util.*

/**
 * 高德地图
 * Created by jhj on 18-9-17.
 */

open class AMapActivity : Activity() {

    private var aMap: AMap? = null
    /**
     * 第一次进地图界面中心点显示传递过来的经纬度，地图移动后显示变化后吃中心点
     */
    private var isFirstEnter = true

    /**
     * 确认时返回的位置信息
     */
    var resultLocation: AMapPositionBean = AMapPositionBean()

    open val layoutRes: Int? = null
    open val isNeedSearch = true
    open val isShowCenterPos = true
    open val scaleSize = 16f
    open val isZoomControlsEnabled = true
    open val isMyLocationButtonEnabled = false
    open val isCompassEnabled = false
    open val isScaleControlsEnabled = false
    open val locationIconRes = R.drawable.map_image_zbcx_iconc


    companion object {
        const val LAT_LON = "latlon"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amap)

        map_view.onCreate(savedInstanceState)
        if (aMap == null) {
            aMap = map_view.map
        }

        location()

        if (isNeedSearch) {
            layout_map_search.visibility = View.VISIBLE
            locationSearch()
        } else {
            layout_map_search.visibility = View.GONE
        }

        if (isShowCenterPos) {
            layout_amap_center_pos.visibility = View.VISIBLE
        } else {
            layout_amap_center_pos.visibility = View.GONE
        }

        layoutRes?.let {
            val view = LayoutInflater.from(this).inflate(it, layout_amap_top_bar, false)
            initAMapTopBar(view)
            layout_amap_top_bar.addView(view)
        }

        //地图移动监听
        aMap?.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {
            override fun onCameraChange(cameraPosition: CameraPosition) {

            }

            override fun onCameraChangeFinish(cameraPosition: CameraPosition) {
                onMapRemove(cameraPosition.target)

            }
        })


    }

    open fun initAMapTopBar(view: View) {

    }

    private fun locationSearch() {
        iv_amap_search_back.setOnClickListener {
            closeKeyboard(layout_amap_search_pos)
            layout_amap_search_pos.visibility = View.GONE
        }
        recyclerView_amap_search_pos.layoutManager = LinearLayoutManager(this)
        recyclerView_amap_search_pos.addItemDecoration(DividerLineItemDecoration(this))
        val adapter = AMapSearchPositionAdapter(this)
        et_amap_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                val query = InputtipsQuery(s.toString(), null)
                query.cityLimit = false//是否限制当前城市
                val inputTips = Inputtips(this@AMapActivity, query)
                inputTips.setInputtipsListener { list, _ ->
                    adapter.dataList = list as ArrayList<Tip>
                    recyclerView_amap_search_pos.adapter = adapter
                }
                inputTips.requestInputtipsAsyn()
            }
        })

        tv_amap_search.setOnClickListener {
            layout_amap_search_pos.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(this@AMapActivity, R.anim.anim_in_bottom)
            recyclerView_amap_search_pos.startAnimation(animation)
            openKeyboard(et_amap_search)
            et_amap_search.isFocusable = true
            et_amap_search.isFocusableInTouchMode = true
            et_amap_search.requestFocus()
        }
    }


    private fun location() {
        //定位蓝点样式
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) //只定位一次
        myLocationStyle.strokeWidth(0f)//设置定位蓝点精度圈的边框宽度的方法。
        myLocationStyle.strokeColor(Color.TRANSPARENT)//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(Color.TRANSPARENT)//设置定位蓝点精度圆圈的填充颜色的方法。
        myLocationStyle.showMyLocation(false)//用于满足只想使用定位，不想使用定位小蓝点的场景
        //myLocationStyle.myLocationIcon(null);
        //myLocationStyle.interval(2000); //设置连续定位间隔，只在连续定位模式下生效

        //设置定位蓝点的Style
        aMap?.setMyLocationStyle(myLocationStyle)
        aMap?.isMyLocationEnabled = true// 是否启用定位蓝点，默认是false，不定位不显示蓝点。

        //缩放按钮是否显示,默认显示且在右下
        aMap?.uiSettings?.isZoomControlsEnabled = isZoomControlsEnabled
        aMap?.uiSettings?.zoomPosition = AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM

        //定位按钮是否显示，默认不显示
        aMap?.uiSettings?.isMyLocationButtonEnabled = isMyLocationButtonEnabled

        //指南针是否显示，默认不显示
        aMap?.uiSettings?.isCompassEnabled = isCompassEnabled

        //控制比例尺控件是否显示
        aMap?.uiSettings?.isScaleControlsEnabled = isScaleControlsEnabled


        //经过定位，获取经纬度信息：
        aMap?.setOnMyLocationChangeListener { location ->
            val mIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, locationIconRes))
            val bundle = location.extras
            val province = bundle.getString("Province")
            val city = bundle.getString("City")
            val district = bundle.getString("District")
            val address = bundle.getString("Address")
            val errorCode = bundle.getInt("errorCode")

            if (errorCode == 0) {
                iv_amap_location.visibility = View.VISIBLE
                iv_amap_location.setOnClickListener {
                    aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), scaleSize))
                }
            } else {
                iv_amap_location.visibility = View.GONE
            }

            when (errorCode) {
                0 -> {
                    val targetPosition = intent.getParcelableExtra(LAT_LON) as LatLng?
                    if (targetPosition != null) {
                        aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(targetPosition.latitude, targetPosition.longitude), scaleSize))
                    } else {
                        aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), scaleSize))
                    }

                    val list = address(province, city, district, address)
                    aMap?.addMarker(MarkerOptions()
                            .position(LatLng(location.latitude, location.longitude))
                            .title(subString(list[0]))
                            .snippet(subString(list[1]))
                            .icon(mIcon)
                            .draggable(true)
                            .period(50))
                            ?.showInfoWindow()
                }
                12 -> {
                    val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    ActivityCompat.requestPermissions(this@AMapActivity, permissions, 1000)
                }
                else -> {
                    aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(39.908823, 116.397470), scaleSize))
                    aMap?.addMarker(MarkerOptions()
                            .position(LatLng(39.908823, 116.397470))
                            .title("北京市东城区")
                            .snippet("东华门街道天安门")
                            .icon(mIcon)
                            .draggable(true)
                            .period(50))
                            ?.showInfoWindow()
                }
            }


        }
    }

    private fun onMapRemove(target: LatLng) {
        val targetPosition = intent.getParcelableExtra(LAT_LON) as LatLng?
        val center = if (targetPosition != null && isFirstEnter) {
            targetPosition
        } else {
            target
        }
        isFirstEnter = false

        ParsePlaceUtil(this@AMapActivity).getAddress(LatLonPoint(center.latitude, center.longitude)) { result, _ ->
            if (result != null && "" != result.formatAddress) {
                parseResultPosition(result, center)
                val mProvince = result.province
                val mCity = result.city
                val mDistrict = result.district
                val mAddress = result.formatAddress
                val list = address(mProvince, mCity, mDistrict, mAddress)
                tv_amap_center_pos.visibility = View.VISIBLE
                if ("" == list[1]) {
                    tv_amap_center_pos.text = subString(list[0])
                } else {
                    tv_amap_center_pos.text = String.format(Locale.CHINA, "%s\n%s", subString(list[0]), subString(list[1]))
                }
                tv_amap_center_pos.text = String.format(Locale.CHINA, "%s\n%s", subString(list[0]), subString(list[1]))
            } else {
                tv_amap_center_pos.visibility = View.GONE
            }
        }
    }

    private fun parseResultPosition(result: RegeocodeAddress, center: LatLng) {
        resultLocation.latitude = center.latitude
        resultLocation.longitude = center.longitude
        resultLocation.adCode = result.adCode
        resultLocation.province = result.province
        resultLocation.city = result.city
        resultLocation.cityCode = result.cityCode
        resultLocation.district = result.district
        resultLocation.address = result.formatAddress
        resultLocation.neighborhood = result.neighborhood
        resultLocation.townCode = result.towncode
        resultLocation.townShop = result.township
    }

    private fun address(province: String?, city: String?, district: String?, address: String?): ArrayList<String> {
        val title: String?
        val snippet: String
        val arrayList = ArrayList<String>()
        if ("北京市" == province || "上海市" == province || "天津市" == province || "重庆市" == province) {
            title = province + district
            snippet = address!!.substring(title.length, address.length)
        } else {
            if (city != null && district != null) {
                title = city + district
                snippet = address!!.substring((province!! + title).length, address.length)
            } else if (city == null && district != null) {
                title = district
                snippet = address!!.substring((province!! + title).length, address.length)
            } else {
                title = city
                snippet = address!!.substring((province!! + title!!).length, address.length)
            }
        }
        arrayList.add(title)
        arrayList.add(snippet)
        return arrayList
    }

    private fun subString(str: String): String {
        var divideLength = 20
        val builder = StringBuilder(str)
        while (divideLength < builder.length) {
            builder.insert(divideLength, '\n')
            divideLength += 21
        }
        return builder.toString()
    }


    fun searchPositionOnMap(tip: Tip) {
        layout_amap_search_pos.visibility = View.GONE
        closeKeyboard(layout_amap_search_pos)
        val latLon = tip.point
        if (latLon != null) {
            aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latLon.latitude, latLon.longitude), scaleSize))
        } else {
            val query = PoiSearch.Query(tip.name, "", null)
            //keyWord表示搜索字符串，
            //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
            //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
            query.pageSize = 10// 设置每页最多返回多少条poiitem
            query.pageNum = 0//设置查询页码
            val poiSearch = PoiSearch(this, query)
            poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
                override fun onPoiSearched(poiResult: PoiResult, i: Int) {
                    val list = poiResult.pois
                    if (list.size > 0) {
                        val ll = list[0].latLonPoint
                        aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(ll.latitude, ll.longitude), scaleSize))
                    }
                }

                override fun onPoiItemSearched(poiItem: PoiItem, i: Int) {
                }
            })
            poiSearch.searchPOIAsyn()

        }

    }

    private fun openKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

    }

    private fun closeKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this@AMapActivity, "定位权限请求失败", Toast.LENGTH_SHORT).show()
            } else {
                location()
            }
        }

    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (layout_amap_search_pos.visibility == View.VISIBLE) {
                layout_amap_search_pos.visibility = View.GONE
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }


}
