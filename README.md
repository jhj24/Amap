# Amap

## AndroidManifest
- 高德地图key
```
<!-- 高德定位开始 -->
<meta-data
    android:name="com.amap.api.v2.apikey"
    android:value="6f62bcccf04331024328f8d11201d63b" />
<!--高德定位结束-->
```

- 权限
```
<!--用于进行网络定位-->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<!--用于访问GPS定位-->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<!--用于获取运营商信息，用于支持提供运营商信息相关的接口-->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<!--用于访问wifi网络信息，wifi信息会用于进行网络定位-->
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<!--用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<!--用于访问网络，网络定位需要上网-->
<uses-permission android:name="android.permission.INTERNET" />
<!--用于读取手机当前的状态-->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<!--用于写入缓存数据到扩展存储卡-->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<!--用于申请调用A-GPS模块-->
<uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS" />
<!--用于申请获取蓝牙信息进行室内定位-->
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```


## 定位
- 在Application中初始化定位
- 在需要定位的位置启动定位
- 设置定位监听器

## 地图
- 新建类继承ApamActivity
- 设置标题（layoutRes、initAMapTopBar(view: View)）
- 设置样式
```
    open val isNeedSearch = true　//是否显示搜索框
    open val isShowCenterPos = true　//是否显示中心点位置信息
    open val scaleSize = 16f　//地图界面大小
    open val isZoomControlsEnabled = true　//是否显示缩放按钮
    open val isCompassEnabled = false　//指南针是否显示
    open val isScaleControlsEnabled = false　//比例尺是否显示
    open val locationIconRes = R.drawable.amap_image_zbcx_iconc　//定位点的图标
    open val centerIconRes = R.drawable.amap_image_zbcx_icona　//中心点图标
```

## 混淆
\#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

\# 搜索
-keep   class com.amap.api.services.**{*;}

\#2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

