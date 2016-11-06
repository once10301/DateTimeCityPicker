package com.ly.datetimecitypicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimePicker implements MyNumberPicker.OnValueChangeListener {

    private Context mContext;
    private PopupWindow mPwHome;
    private View mPPWView;
    private MyNumberPicker mMNPHour;
    private MyNumberPicker mMNPMinute;
    private TextView mTVConfirm;
    private TextView mTVCancel;
    protected int mCurrentHour;
    protected int mCurrentMinute;
    private OnTimeClickListener mListener;
    private Calendar mCalendar;

    public interface OnTimeClickListener {
        void onSelected(String... citySelected);
    }

    public void setOnTimeClickListener(OnTimeClickListener listener) {
        this.mListener = listener;
    }

    private String mTextColor;
    private int mTextSize;
    private String mFlagColor;
    private int mFlagSize;
    private int mVisibleItems;
    private int mPadding;
    private String cancelTextColor;
    private String confirmTextColor;
    private int mDefaultHour;
    private int mDefaultMinute;
    private boolean isSound;
    private String mBackground;

    private TimePicker(Builder builder) {
        this.mContext = builder.mContext;
        this.mTextColor = builder.mTextColor;
        this.mTextSize = builder.mTextSize;
        this.mFlagColor = builder.mFlagColor;
        this.mFlagSize = builder.mFlagSize;
        this.mVisibleItems = builder.mVisibleItems;
        this.mPadding = builder.mPadding;
        this.confirmTextColor = builder.confirmTextColor;
        this.cancelTextColor = builder.cancelTextColor;
        this.mDefaultHour = builder.mDefaultHour;
        this.mDefaultMinute = builder.mDefaultMinute;
        this.isSound = builder.isSound;
        this.mBackground = builder.mBackground;

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mPPWView = layoutInflater.inflate(R.layout.time_picker, null);
        mMNPHour = (MyNumberPicker) mPPWView.findViewById(R.id.mnpHour);
        mMNPMinute = (MyNumberPicker) mPPWView.findViewById(R.id.mnpMinute);
        mTVCancel = (TextView) mPPWView.findViewById(R.id.tvCancel);
        mTVConfirm = (TextView) mPPWView.findViewById(R.id.tvConfirm);
        TextView tvHour = (TextView) mPPWView.findViewById(R.id.tvHour);
        TextView tvMinute = (TextView) mPPWView.findViewById(R.id.tvMinute);
        mPwHome = new PopupWindow(mPPWView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mPwHome.setBackgroundDrawable(new ColorDrawable(0x80000000));
        mPwHome.setTouchable(true);
        mPwHome.setOutsideTouchable(true);
        mPwHome.setFocusable(true);

        // 设置确认按钮文字颜色
            mTVConfirm.setTextColor(Color.parseColor(this.confirmTextColor));
        // 设置取消按钮文字颜色
            mTVCancel.setTextColor(Color.parseColor(this.cancelTextColor));

        // 设置item文字颜色
        mMNPHour.setTextColor(Color.parseColor(mTextColor));
        mMNPMinute.setTextColor(Color.parseColor(mTextColor));

        // 设置item文字大小
        mMNPHour.setTextSize(mTextSize);
        mMNPMinute.setTextSize(mTextSize);

        // 设置年月日文字颜色
        tvHour.setTextColor(Color.parseColor(mFlagColor));
        tvMinute.setTextColor(Color.parseColor(mFlagColor));

        // 设置年月日文字大小
        tvHour.setTextSize(mFlagSize);
        tvMinute.setTextSize(mFlagSize);

        // 滚轮显示的item个数
        mMNPHour.setRowNumber(mVisibleItems);
        mMNPMinute.setRowNumber(mVisibleItems);

        // item间距
        mMNPHour.setPadding(mPadding);
        mMNPMinute.setPadding(mPadding);

        // 设置滑动时声效
        if (isSound) {
            Sound sound = new Sound(mContext);
            mMNPHour.setSoundEffect(sound);
            mMNPMinute.setSoundEffect(sound);
        }

        // 设置背景色
        mMNPHour.setBackground(Color.parseColor(mBackground));
        mMNPMinute.setBackground(Color.parseColor(mBackground));

        mMNPHour.setOnValueChangeListener(this);
        mMNPMinute.setOnValueChangeListener(this);
        mCalendar = Calendar.getInstance();
        setDate(mCalendar.getTime());

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
                String hour = mCurrentHour < 10 ? "0" + mCurrentHour : mCurrentHour + "";
                String minute = mCurrentMinute < 10 ? "0" + mCurrentMinute : mCurrentMinute + "";
                mListener.onSelected(hour, minute);
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
        private int mTextSize = 20;
        /**
         * Default flag color
         */
        private String mFlagColor = "#16b3f4";
        /**
         * Default flag size
         */
        private int mFlagSize = 20;
        /**
         * 滚轮显示的item个数
         */
        private int mVisibleItems = 7;
        /**
         * item间距
         */
        private int mPadding = 20;
        /**
         * 取消按钮颜色
         */
        private String cancelTextColor = "#333333";
        /**
         * 确认按钮颜色
         */
        private String confirmTextColor = "#16b3f4";
        /**
         * 第一次默认显示的时
         */
        private int mDefaultHour = -1;
        /**
         * 第一次默认显示的分
         */
        private int mDefaultMinute = -1;
        /**
         * 滑动时是否有声效
         */
        private boolean isSound = true;
        /**
         * Default background color
         */
        private String mBackground = "#ffffff";

        public Builder(Context context) {
            this.mContext = context;
        }

        public static SimpleDateFormat mFormat;

        /**
         * 默认显示的时间
         */
        public Builder time(String defaultDate) {
            mFormat = new SimpleDateFormat("HH:mm");
            mFormat.setLenient(false);
            if (isValidDate(defaultDate)) {
                mDefaultHour = Integer.valueOf(defaultDate.split(":")[0]);
                mDefaultMinute = Integer.valueOf(defaultDate.split(":")[1]);
            }
            return this;
        }

        public static boolean isValidDate(String s) {
            try {
                mFormat.parse(s);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 确认按钮文字颜色
         *
         * @param color
         * @return
         */
        public Builder confirmTextColor(String color) {
            this.confirmTextColor= color;
            return this;
        }

        /**
         * 取消按钮文字颜色
         *
         * @param color
         * @return
         */
        public Builder cancelTextColor(String color) {
            this.cancelTextColor = color;
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
         * 时分文字颜色
         *
         * @param flagColor
         * @return
         */
        public Builder flagColor(String flagColor) {
            this.mFlagColor = flagColor;
            return this;
        }

        /**
         * 时分文字大小
         *
         * @param flagSize
         * @return
         */
        public Builder flagSize(int flagSize) {
            this.mFlagSize = flagSize;
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
        public Builder isSound(boolean isSound) {
            this.isSound = isSound;
            return this;
        }

        /**
         * item文字颜色
         *
         * @param background
         * @return
         */
        public Builder setBackground(String background) {
            this.mBackground = background;
            return this;
        }

        public TimePicker build() {
            TimePicker cityPicker = new TimePicker(this);
            return cityPicker;
        }

    }

    public void setDate(Date date) {
        mCalendar.setTime(date);
        if (mDefaultHour != -1) {
            mMNPHour.setCurrentNumber(mDefaultHour);
            mCurrentHour = mDefaultHour;
            mCalendar.set(Calendar.HOUR_OF_DAY, mDefaultHour);
        } else {
            mMNPHour.setCurrentNumber(mCalendar.get(Calendar.HOUR_OF_DAY));
            mCurrentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        }
        if (mDefaultMinute != -1) {
            mMNPMinute.setCurrentNumber(mDefaultMinute);
            mCurrentMinute = mDefaultMinute;
            mCalendar.set(Calendar.MINUTE, mDefaultMinute - 1);
        } else {
            mMNPMinute.setCurrentNumber(mCalendar.get(Calendar.MINUTE));
            mCurrentMinute = mCalendar.get(Calendar.MINUTE);
        }
    }

    public void show() {
        if (!mPwHome.isShowing()) {
            setDate(mCalendar.getTime());
            mPwHome.showAtLocation(mPPWView, Gravity.BOTTOM, 0, 0);
        }
    }

    public void hide() {
        if (mPwHome.isShowing()) {
            mPwHome.dismiss();
        }
    }

    @Override
    public void onValueChange(MyNumberPicker picker, int oldVal, int newVal) {
        if (picker == mMNPHour) {
            mCalendar.set(Calendar.HOUR_OF_DAY, newVal);
            mCurrentHour = newVal;
        } else if (picker == mMNPMinute) {
            mCalendar.set(Calendar.MINUTE, newVal);
            mCurrentMinute = newVal;
        }
    }

}
