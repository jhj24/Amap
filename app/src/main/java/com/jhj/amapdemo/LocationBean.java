package com.jhj.amapdemo;

import java.io.Serializable;

/**
 * 高德地图返回的位置信息
 * Created by jhj on 18-10-19.
 */

public class LocationBean implements Serializable {

    /**
     * 错误码
     */
    private int errorCode;
    /**
     * 错误信息
     */
    private String errorInfo;
    /**
     * 定位时间
     */
    private long time;
    /**
     * 纬度（垂直方向）
     */
    private double latitude;
    /**
     * 经度（水平方向）
     */
    private double longitude;
    /**
     * 县区编码
     */
    private String adCode;
    /**
     * 所在国家
     */
    private String country;
    /**
     * 　省、直辖市名称
     */
    private String province;
    /**
     * 　所在城市
     */
    private String city;
    /**
     * 　所在城市编码
     */
    private String cityCode;
    /**
     * 所在区县
     */
    private String district;
    /**
     * 格式化的地址
     */
    private String address;
    /**
     * 社区名称
     */
    private String neighborhood;
    /**
     * 乡镇编码
     */
    private String townCode;
    /**
     * 乡镇名称
     */
    private String townShop;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getTownCode() {
        return townCode;
    }

    public void setTownCode(String townCode) {
        this.townCode = townCode;
    }

    public String getTownShop() {
        return townShop;
    }

    public void setTownShop(String townShop) {
        this.townShop = townShop;
    }
}
