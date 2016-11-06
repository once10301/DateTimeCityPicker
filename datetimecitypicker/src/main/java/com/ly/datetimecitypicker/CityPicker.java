package com.ly.datetimecitypicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ly.datetimecitypicker.model.City;
import com.ly.datetimecitypicker.model.County;
import com.ly.datetimecitypicker.model.Province;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CityPicker implements MyNumberPicker.OnValueChangeListener {

    private Context mContext;
    private PopupWindow mPPWHome;
    private View mPPWView;
    private MyNumberPicker mMNPProvince;
    private MyNumberPicker mMNPCity;
    private MyNumberPicker mMNPCounty;
    private TextView mTVConfirm;
    private TextView mTVCancel;
    private List<Province> mProvinceList = new ArrayList<>();
    private List<City> mCityList = new ArrayList<>();
    private List<County> mCountyList = new ArrayList<>();
    protected String mCurrentProvince;
    protected String mCurrentCity;
    protected String mCurrentCounty;
    protected String mCurrentID;
    private OnCityItemClickListener listener;

    public interface OnCityItemClickListener {
        void onSelected(String... citySelected);
    }

    public void setOnCityItemClickListener(OnCityItemClickListener listener) {
        this.listener = listener;
    }

    private String mTextColor;
    private int mTextSize;
    private boolean isProvinceCyclic = false;
    private boolean isCityCyclic = false;
    private boolean isCountyCyclic = false;
    private int mVisibleItems;
    private int mPadding;
    private String mCancelTextColor;
    private String mConfirmTextColor;
    private String mDefaultProvince;
    private String mDefaultCity;
    private String mDefaultCounty;
    private boolean isSound;
    private String mBackground;
    private boolean showProvinceAndCity = false;

    private CityPicker(Builder builder) {
        this.mContext = builder.mContext;
        this.mTextColor = builder.mTextColor;
        this.mTextSize = builder.mTextSize;
        this.isProvinceCyclic = builder.isProvinceCyclic;
        this.isCityCyclic = builder.isCityCyclic;
        this.isCountyCyclic = builder.isCountyCyclic;
        this.mVisibleItems = builder.mVisibleItems;
        this.mPadding = builder.mPadding;
        this.mCancelTextColor = builder.mCancelTextColor;
        this.mConfirmTextColor = builder.mConfirmTextColor;
        this.mDefaultProvince = builder.mDefaultProvince;
        this.mDefaultCity = builder.mDefaultCity;
        this.mDefaultCounty = builder.mDefaultCounty;
        this.isSound = builder.isSound;
        this.mBackground = builder.mBackground;
        this.showProvinceAndCity = builder.showProvinceAndCity;

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mPPWView = layoutInflater.inflate(R.layout.city_picker, null);

        mMNPProvince = (MyNumberPicker) mPPWView.findViewById(R.id.mnpProvince);
        mMNPCity = (MyNumberPicker) mPPWView.findViewById(R.id.mnpCity);
        mMNPCounty = (MyNumberPicker) mPPWView.findViewById(R.id.mnpCounty);
        mTVConfirm = (TextView) mPPWView.findViewById(R.id.tvConfirm);
        mTVCancel = (TextView) mPPWView.findViewById(R.id.tvCancel);

        mPPWHome = new PopupWindow(mPPWView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mPPWHome.setBackgroundDrawable(new ColorDrawable(0x80000000));
        mPPWHome.setTouchable(true);
        mPPWHome.setOutsideTouchable(true);
        mPPWHome.setFocusable(true);

        //设置确认按钮文字颜色
        mTVConfirm.setTextColor(Color.parseColor(this.mConfirmTextColor));
        //设置取消按钮文字颜色
        mTVCancel.setTextColor(Color.parseColor(this.mCancelTextColor));

        // 设置item文字颜色
        mMNPProvince.setTextColor(Color.parseColor(mTextColor));
        mMNPCity.setTextColor(Color.parseColor(mTextColor));
        mMNPCounty.setTextColor(Color.parseColor(mTextColor));

        // 设置item文字大小
        mMNPProvince.setTextSize(mTextSize);
        mMNPCity.setTextSize(mTextSize);
        mMNPCounty.setTextSize(mTextSize);

        // 滚轮显示的item个数
        mMNPProvince.setRowNumber(mVisibleItems);
        mMNPCity.setRowNumber(mVisibleItems);
        mMNPCounty.setRowNumber(mVisibleItems);

        // item间距
        mMNPProvince.setPadding(mPadding);
        mMNPCity.setPadding(mPadding);
        mMNPCounty.setPadding(mPadding);

        // 设置滑动时声效
        if (isSound) {
            Sound sound = new Sound(mContext);
            mMNPProvince.setSoundEffect(sound);
            mMNPCity.setSoundEffect(sound);
            mMNPCounty.setSoundEffect(sound);
        }

        // 设置背景色
        mMNPProvince.setBackground(Color.parseColor(mBackground));
        mMNPCity.setBackground(Color.parseColor(mBackground));
        mMNPCounty.setBackground(Color.parseColor(mBackground));

        //只显示省市两级联动
        if (this.showProvinceAndCity) {
            mMNPCounty.setVisibility(View.GONE);
        } else {
            mMNPCounty.setVisibility(View.VISIBLE);
        }

        //初始化所有地区数据
        initAllDatas(mContext);

        mMNPProvince.setOnValueChangeListener(this);
        mMNPCity.setOnValueChangeListener(this);
        mMNPCounty.setOnValueChangeListener(this);
        // 添加onclick事件
        mTVCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        mTVConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showProvinceAndCity) {
                    listener.onSelected(mCurrentProvince, mCurrentCity, "", mCurrentID);
                } else {
                    listener.onSelected(mCurrentProvince, mCurrentCity, mCurrentCounty, mCurrentID);
                }
                hide();
            }
        });

    }

    public static class Builder {
        private Context mContext;
        /**
         * Default text color
         */
        private String mTextColor = "#16b3f4";
        /**
         * Default text size
         */
        private int mTextSize = 18;
        /**
         * 滚轮显示的item个数
         */
        private int mVisibleItems = 7;
        /**
         * 省市滚轮是否循环滚动
         */
        private boolean isProvinceCyclic = false;
        /**
         * 市区滚轮是否循环滚动
         */
        private boolean isCityCyclic = false;
        /**
         * 县滚轮是否循环滚动
         */
        private boolean isCountyCyclic = false;
        /**
         * item间距
         */
        private int mPadding = 20;
        /**
         * 取消按钮颜色
         */
        private String mCancelTextColor = "#333333";
        /**
         * 确认按钮颜色
         */
        private String mConfirmTextColor = "#16b3f4";
        /**
         * 第一次默认显示的省市，一般配合定位使用
         */
        private String mDefaultProvince;
        /**
         * 第一次默认显示的市区，一般配合定位使用
         */
        private String mDefaultCity;
        /**
         * 第一次默认显示的县，一般配合定位使用
         */
        private String mDefaultCounty = "";
        /**
         * 滑动时是否有声效
         */
        private boolean isSound = true;
        /**
         * 背景色
         */
        private String mBackground = "#ffffff";
        /**
         * 两级联动
         */
        private boolean showProvinceAndCity = false;

        public Builder(Context context) {
            this.mContext = context;
        }

        /**
         * 是否只显示省市两级联动
         *
         * @param flag
         * @return
         */
        public Builder onlyShowProvinceAndCity(boolean flag) {
            this.showProvinceAndCity = flag;
            return this;
        }

        /**
         * 第一次默认显示的省市，一般配合定位使用
         *
         * @param defaultProvince
         * @return
         */
        public Builder province(String defaultProvince) {
            this.mDefaultProvince = defaultProvince;
            return this;
        }

        /**
         * 第一次默认显示的市区，一般配合定位使用
         *
         * @param defaultCity
         * @return
         */
        public Builder city(String defaultCity) {
            this.mDefaultCity = defaultCity;
            return this;
        }

        /**
         * 第一次默认显示的县，一般配合定位使用
         *
         * @param defaultCounty
         * @return
         */
        public Builder county(String defaultCounty) {
            this.mDefaultCounty = defaultCounty;
            return this;
        }

        /**
         * 确认按钮文字颜色
         *
         * @param color
         * @return
         */
        public Builder confirmTextColor(String color) {
            this.mConfirmTextColor = color;
            return this;
        }

        /**
         * 取消按钮文字颜色
         *
         * @param color
         * @return
         */
        public Builder cancelTextColor(String color) {
            this.mCancelTextColor = color;
            return this;
        }

        /**
         * item文字颜色
         *
         * @param textColor
         * @return
         */
        public Builder textColor(String textColor) {
            this.mTextColor = textColor;
            return this;
        }

        /**
         * item文字大小
         *
         * @param textSize
         * @return
         */
        public Builder textSize(int textSize) {
            this.mTextSize = textSize;
            return this;
        }

        /**
         * 滚轮显示的item个数
         *
         * @param visibleItems
         * @return
         */
        public Builder visibleItemsCount(int visibleItems) {
            this.mVisibleItems = visibleItems;
            return this;
        }

        /**
         * 省滚轮是否循环滚动
         *
         * @param isProvinceCyclic
         * @return
         */
        public Builder provinceCyclic(boolean isProvinceCyclic) {
            this.isProvinceCyclic = isProvinceCyclic;
            return this;
        }

        /**
         * 市滚轮是否循环滚动
         *
         * @param isCityCyclic
         * @return
         */
        public Builder cityCyclic(boolean isCityCyclic) {
            this.isCityCyclic = isCityCyclic;
            return this;
        }

        /**
         * 县滚轮是否循环滚动
         *
         * @param isCountyCyclic
         * @return
         */
        public Builder countyCyclic(boolean isCountyCyclic) {
            this.isCountyCyclic = isCountyCyclic;
            return this;
        }

        /**
         * item间距
         *
         * @param itemPadding
         * @return
         */
        public Builder itemPadding(int itemPadding) {
            this.mPadding = itemPadding;
            return this;
        }

        /**
         * 滑动时是否有声效
         *
         * @param isSound
         * @return
         */
        public CityPicker.Builder isSound(boolean isSound) {
            this.isSound = isSound;
            return this;
        }

        /**
         * 背景色
         *
         * @param background
         * @return
         */
        public CityPicker.Builder setBackground(String background) {
            this.mBackground = background;
            return this;
        }

        public CityPicker build() {
            CityPicker cityPicker = new CityPicker(this);
            return cityPicker;
        }

    }

    /**
     * 解析所有地区JSON数据
     */
    protected void initAllDatas(Context context) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open("area.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            String result = "";
            while ((line = bufReader.readLine()) != null)
                result += line;
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Province province = new Province();
                province.setAreaId(jsonObject.getString("areaId"));
                province.setAreaName(jsonObject.getString("areaName"));
                JSONArray jsonArray2 = new JSONArray(jsonObject.getString("cities"));
                for (int j = 0; j < jsonArray2.length(); j++) {
                    JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
                    City city = new City();
                    city.setAreaId(jsonObject2.getString("areaId"));
                    city.setAreaName(jsonObject2.getString("areaName"));
                    JSONArray jsonArray3 = new JSONArray(jsonObject2.getString("counties"));
                    for (int k = 0; k < jsonArray3.length(); k++) {
                        JSONObject jsonObject3 = jsonArray3.getJSONObject(k);
                        County county = new County();
                        county.setAreaId(jsonObject3.getString("areaId"));
                        county.setAreaName(jsonObject3.getString("areaName"));
                        city.counties.add(county);
                    }
                    province.cities.add(city);
                }
                mProvinceList.add(province);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新省市WheelView的信息
     */
    private void updateProvinces() {
        int provinceDefault = -1;
        if (!TextUtils.isEmpty(mDefaultProvince) && mProvinceList.size() > 0) {
            for (int i = 0; i < mProvinceList.size(); i++) {
                if (mProvinceList.get(i).getAreaName().contains(mDefaultProvince)) {
                    provinceDefault = i;
                    break;
                }
            }
        }
        List<String> list = new ArrayList<>();
        for (Province province : mProvinceList) {
            list.add(province.getAreaName());
        }
        String[] provinces = list.toArray(new String[list.size()]);
        mMNPProvince.setEndNumber(provinces.length);
        mMNPProvince.setCustomTextArray(provinces);
        //获取所设置的省的位置，直接定位到该位置
        if (-1 != provinceDefault) {
            mMNPProvince.setCurrentNumber(provinceDefault + 1);
        }
        updateCities();
    }

    /**
     * 根据当前的省市，更新市区WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mMNPProvince.getCurrentNumber() - 1;
        mCurrentProvince = mProvinceList.get(pCurrent).getAreaName();
        mCityList = mProvinceList.get(pCurrent).getCities();
        List<String> list = new ArrayList<>();
        for (City city : mCityList) {
            list.add(city.getAreaName());
        }
        String[] cities = list.toArray(new String[list.size()]);
        int cityDefault = -1;
        if (!TextUtils.isEmpty(mDefaultCity) && cities.length > 0) {
            for (int i = 0; i < cities.length; i++) {
                if (cities[i].contains(mDefaultCity)) {
                    cityDefault = i;
                    break;
                }
            }
        }
        mMNPCity.setEndNumber(cities.length);
        mMNPCity.setCustomTextArray(cities);
        if (-1 != cityDefault) {
            mMNPCity.setCurrentNumber(cityDefault + 1);
            mCurrentCity = mCityList.get(cityDefault).getAreaName();
            mCurrentID = mCityList.get(cityDefault).getAreaId();
        } else {
            mMNPCity.setCurrentNumber(0);
            mCurrentCity = mCityList.get(0).getAreaName();
            mCurrentID = mCityList.get(0).getAreaId();
        }
        if (!showProvinceAndCity) {
            updateCounties();
        }
    }

    /**
     * 根据当前的市区，更新县WheelView的信息
     */
    private void updateCounties() {
        // 清除之前的
        mCurrentCounty = "";
        mCurrentID = "";

        int pCurrent = mMNPCity.getCurrentNumber() - 1;
        mCurrentCity = mCityList.get(pCurrent).getAreaName();
        mCurrentID = mCityList.get(pCurrent).getAreaId();
        mCountyList = mCityList.get(pCurrent).getCounties();
        List<String> list = new ArrayList<>();
        for (County county : mCountyList) {
            list.add(county.getAreaName());
        }
        String[] counties = list.toArray(new String[list.size()]);
        if (counties.length == 0) {
            counties = new String[]{""};
        }
        int countyDefault = -1;
        if (!TextUtils.isEmpty(mDefaultCounty) && counties.length > 0) {
            for (int i = 0; i < counties.length; i++) {
                if (counties[i].contains(mDefaultCounty)) {
                    countyDefault = i;
                    break;
                }
            }
        }
        mMNPCounty.setEndNumber(counties.length);
        mMNPCounty.setCustomTextArray(counties);
        if (-1 != countyDefault) {
            mMNPCounty.setCurrentNumber(countyDefault + 1);
            mCurrentCounty = mCountyList.get(countyDefault).getAreaName();
            mCurrentID = mCountyList.get(countyDefault).getAreaId();
        } else {
            mMNPCounty.setCurrentNumber(0);
            if (mCountyList.size() > 0) {
                mCurrentCounty = mCountyList.get(0).getAreaName();
                mCurrentID = mCountyList.get(0).getAreaId();
            }
        }
    }

    public void show() {
        if (!mPPWHome.isShowing()) {
            updateProvinces();
            mPPWHome.showAtLocation(mPPWView, Gravity.BOTTOM, 0, 0);
        }
    }

    public void hide() {
        if (mPPWHome.isShowing()) {
            mPPWHome.dismiss();
        }
    }

    @Override
    public void onValueChange(MyNumberPicker picker, int oldVal, int newVal) {
        if (picker == mMNPProvince) {
            updateCities();
        } else if (picker == mMNPCity) {
            updateCounties();
        } else if (picker == mMNPCounty) {
            int current = mMNPCounty.getCurrentNumber() - 1;
            mCurrentCounty = mCountyList.get(current).getAreaName();
            mCurrentID = mCountyList.get(current).getAreaId();
        }
    }

}
