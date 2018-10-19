package com.jhj.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 高德地图
 * Created by jhj on 18-9-17.
 */

public class Amap extends Activity {

    private AMap aMap;
    private MapView mapView;

    private String mProvince;
    private String mCity;
    private String mDistrict;
    private String mAddress;
    private double mLat;
    private double mLon;
    public Location mLocation;
    private LinearLayout layoutPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        moveToCenters();
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                onMapRemove(cameraPosition.target);
            }
        });
        findViewById(R.id.iv_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 16f));
            }
        });

        layoutPosition = findViewById(R.id.linearLayout_amap_position);
        final TextView tvSearch = findViewById(R.id.tv_search);
        final EditText etSearch = findViewById(R.id.et_amap_search);
        final RecyclerView recyclerView = findViewById(R.id.recyclerView_amap_position);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerLineItemDecoration(this));
        final AMapSearchPositionAdapter adapter = new AMapSearchPositionAdapter(this);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                InputtipsQuery query = new InputtipsQuery(s.toString(), null);
                query.setCityLimit(false);//是否限制当前城市
                Inputtips inputtips = new Inputtips(Amap.this, query);
                inputtips.setInputtipsListener(new Inputtips.InputtipsListener() {
                    @Override
                    public void onGetInputtips(List<Tip> list, int i) {
                        adapter.setDataList((ArrayList<Tip>) list);
                        recyclerView.setAdapter(adapter);

                    }
                });
                inputtips.requestInputtipsAsyn();
            }
        });
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutPosition.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(Amap.this, R.anim.anim_in_bottom);
                findViewById(R.id.recyclerView_amap_position).startAnimation(animation);
                openKeyboard(etSearch);
                etSearch.setFocusable(true);
                etSearch.setFocusableInTouchMode(true);
                etSearch.requestFocus();
            }
        });

    }


    private void moveToCenters() {
        //定位蓝点样式
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE); //只定位一次
        myLocationStyle.strokeWidth(0);//设置定位蓝点精度圈的边框宽度的方法。
        myLocationStyle.strokeColor(Color.TRANSPARENT);//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);//设置定位蓝点精度圆圈的填充颜色的方法。
        myLocationStyle.showMyLocation(false);//是否显示定位图标
        //myLocationStyle.myLocationIcon(null);
        //myLocationStyle.interval(2000); //设置连续定位间隔，只在连续定位模式下生效
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 是否启用定位蓝点，默认是false。

        //缩放按钮是否显示,默认显示且在右下
        aMap.getUiSettings().setZoomControlsEnabled(true);
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);

        //定位按钮是否显示，默认不显示
        aMap.getUiSettings().setMyLocationButtonEnabled(false);


        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {


            @Override
            public void onMyLocationChange(Location location) {
                mLocation = location;
                BitmapDescriptor mIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_image_zbcx_iconc));
                Bundle bundle = location.getExtras();
                String province = bundle.getString("Province");
                String city = bundle.getString("City");
                String district = bundle.getString("District");
                String address = bundle.getString("Address");
                int errorCode = bundle.getInt("errorCode");

                if (errorCode == 0) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16f));
                    ArrayList<String> list = address(province, city, district, address);
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .title(subString(list.get(0)))
                            .snippet(subString(list.get(1)))
                            .icon(mIcon)
                            .draggable(true)
                            .period(50))
                            .showInfoWindow();
                } else {
                    if (errorCode == 12) {
                        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                        ActivityCompat.requestPermissions(Amap.this, permissions, 1000);
                    }
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.908823, 116.397470), 16f));
                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(39.908823, 116.397470))
                            .title("北京市东城区")
                            .snippet("东华门街道天安门")
                            .icon(mIcon)
                            .draggable(true)
                            .period(50))
                            .showInfoWindow();

                }
            }
        });
    }

    private void onMapRemove(final LatLng target) {
        new ParsePlaceUtil(Amap.this).getAddress(new LatLonPoint(target.latitude, target.longitude), new ParsePlaceUtil.OnParseLatLonListener() {
            @Override
            public void callback(RegeocodeAddress result, int code) {
                TextView textView = findViewById(R.id.tv_amap_location);
                if (result != null && !"".equals(result.getFormatAddress())) {
                    mLat = target.latitude;
                    mLon = target.longitude;
                    mProvince = result.getProvince();
                    mCity = result.getCity();
                    mDistrict = result.getDistrict();
                    mAddress = result.getFormatAddress();
                    ArrayList<String> list = address(mProvince, mCity, mDistrict, mAddress);
                    textView.setVisibility(View.VISIBLE);
                    if ("".equals(list.get(1))) {
                        textView.setText(subString(list.get(0)));
                    } else {
                        textView.setText(String.format(Locale.CHINA, "%s\n%s", subString(list.get(0)), subString(list.get(1))));
                    }
                    textView.setText(String.format(Locale.CHINA, "%s\n%s", subString(list.get(0)), subString(list.get(1))));
                } else {
                    textView.setVisibility(View.GONE);
                }
            }
        });
    }

    private ArrayList<String> address(String province, String city, String district, String address) {
        String title;
        String snippet;
        ArrayList<String> arrayList = new ArrayList<>();
        if ("北京市".equals(province) || "上海市".equals(province) || "天津市".equals(province) || "重庆市".equals(province)) {
            title = province + district;
            snippet = address.substring(title.length(), address.length());
        } else {
            if (city != null && district != null) {
                title = city + district;
                snippet = address.substring((province + title).length(), address.length());
            } else if (city == null && district != null) {
                title = district;
                snippet = address.substring((province + title).length(), address.length());
            } else {
                title = city;
                snippet = address.substring((province + title).length(), address.length());
            }
        }
        arrayList.add(title);
        arrayList.add(snippet);
        return arrayList;
    }

    private String subString(String str) {
        int divideLength = 20;
        StringBuilder builder = new StringBuilder(str);
        while (divideLength < builder.length()) {
            builder.insert(divideLength, '\n');
            divideLength += (21);
        }
        return builder.toString();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(Amap.this, "定位权限请求失败", Toast.LENGTH_SHORT).show();
            } else {
                moveToCenters();
            }
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (layoutPosition.getVisibility() == View.VISIBLE) {
                layoutPosition.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void removeMap(@NotNull Tip tip) {
        layoutPosition.setVisibility(View.GONE);
        closeKeyboard(layoutPosition);
        LatLonPoint latLon = tip.getPoint();
        final String[] a = {tip.getPoiID()};
        if (latLon != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLon.getLatitude(), latLon.getLongitude()), 16f));
        } else {
            PoiSearch.Query query = new PoiSearch.Query(tip.getName(), "", null);
            //keyWord表示搜索字符串，
            //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
            //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
            query.setPageSize(10);// 设置每页最多返回多少条poiitem
            query.setPageNum(0);//设置查询页码
            PoiSearch poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                @Override
                public void onPoiSearched(PoiResult poiResult, int i) {
                    ArrayList<PoiItem> list = poiResult.getPois();
                    if (list.size() > 0){
                        LatLonPoint ll = list.get(0).getLatLonPoint();
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ll.getLatitude(),ll.getLongitude()),16f));
                    }

                }

                @Override
                public void onPoiItemSearched(PoiItem poiItem, int i) {
                    PoiItem c = poiItem;
                }
            });
            poiSearch.searchPOIAsyn();

        }

    }

    private void openKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

    }

    private void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*  cityCode
     *  010 - 北京市
     *  022 - 天津市
     *  济源市 city为 null
     *  神农架区 city为null
     *  东莞市 district为null
     */


}
