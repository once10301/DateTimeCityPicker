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

public class DatePicker implements MyNumberPicker.OnValueChangeListener {

    private Context mContext;
    private PopupWindow mPwHome;
    private View mPPWView;
    private MyNumberPicker mMNPYear;
    private MyNumberPicker mMNPMonth;
    private MyNumberPicker mMNDay;
    private TextView mTVConfirm;
    private TextView mTVCancel;
    protected int mCurrentYear;
    protected int mCurrentMonth;
    protected int mCurrentDay;
    private OnDateClickListener mListener;
    private Calendar mCalendar;

    public interface OnDateClickListener {
        void onSelected(String... citySelected);
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
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
    private int mDefaultYear;
    private int mDefaultMonth;
    private int mDefaultDay;
    private boolean isSound;
    private String mBackground;

    private DatePicker(Builder builder) {
        this.mContext = builder.mContext;
        this.mTextColor = builder.mTextColor;
        this.mTextSize = builder.mTextSize;
        this.mFlagColor = builder.mFlagColor;
        this.mFlagSize = builder.mFlagSize;
        this.mVisibleItems = builder.mVisibleItems;
        this.mPadding = builder.mPadding;
        this.confirmTextColor = builder.confirmTextColor;
        this.cancelTextColor = builder.cancelTextColor;
        this.mDefaultYear = builder.mDefaultYear;
        this.mDefaultMonth = builder.mDefaultMonth;
        this.mDefaultDay = builder.mDefaultDay;
        this.isSound = builder.isSound;
        this.mBackground = builder.mBackground;

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mPPWView = layoutInflater.inflate(R.layout.date_picker, null);
        mMNPYear = (MyNumberPicker) mPPWView.findViewById(R.id.mnpYear);
        mMNPMonth = (MyNumberPicker) mPPWView.findViewById(R.id.mnpMonth);
        mMNDay = (MyNumberPicker) mPPWView.findViewById(R.id.mnpDay);
        mTVCancel = (TextView) mPPWView.findViewById(R.id.tvCancel);
        mTVConfirm = (TextView) mPPWView.findViewById(R.id.tvConfirm);
        TextView tvYear = (TextView) mPPWView.findViewById(R.id.tvYear);
        TextView tvMonth = (TextView) mPPWView.findViewById(R.id.tvMonth);
        TextView tvDay = (TextView) mPPWView.findViewById(R.id.tvDay);
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
        mMNPYear.setTextColor(Color.parseColor(mTextColor));
        mMNPMonth.setTextColor(Color.parseColor(mTextColor));
        mMNDay.setTextColor(Color.parseColor(mTextColor));

        // 设置item文字大小
        mMNPYear.setTextSize(mTextSize);
        mMNPMonth.setTextSize(mTextSize);
        mMNDay.setTextSize(mTextSize);

        // 设置年月日文字颜色
        tvYear.setTextColor(Color.parseColor(mFlagColor));
        tvMonth.setTextColor(Color.parseColor(mFlagColor));
        tvDay.setTextColor(Color.parseColor(mFlagColor));

        // 设置年月日文字大小
        tvYear.setTextSize(mFlagSize);
        tvMonth.setTextSize(mFlagSize);
        tvDay.setTextSize(mFlagSize);

        // 滚轮显示的item个数
        mMNPYear.setRowNumber(mVisibleItems);
        mMNPMonth.setRowNumber(mVisibleItems);
        mMNDay.setRowNumber(mVisibleItems);

        // item间距
        mMNPYear.setPadding(mPadding);
        mMNPMonth.setPadding(mPadding);
        mMNDay.setPadding(mPadding);

        // 设置滑动时声效
        if (isSound) {
            Sound sound = new Sound(mContext);
            mMNPYear.setSoundEffect(sound);
            mMNPMonth.setSoundEffect(sound);
            mMNDay.setSoundEffect(sound);
        }

        // 设置背景色
        mMNPYear.setBackground(Color.parseColor(mBackground));
        mMNPMonth.setBackground(Color.parseColor(mBackground));
        mMNDay.setBackground(Color.parseColor(mBackground));

        mMNPYear.setOnValueChangeListener(this);
        mMNPMonth.setOnValueChangeListener(this);
        mMNDay.setOnValueChangeListener(this);
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
                String month = mCurrentMonth < 10 ? "0" + mCurrentMonth : mCurrentMonth + "";
                String day = mCurrentDay < 10 ? "0" + mCurrentDay : mCurrentDay + "";
                mListener.onSelected(mCurrentYear + "", month, day);
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
         * 第一次默认显示的年
         */
        private int mDefaultYear = -1;
        /**
         * 第一次默认显示的月
         */
        private int mDefaultMonth = -1;
        /**
         * 第一次默认显示的日
         */
        private int mDefaultDay = -1;
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
         * 默认显示的日期
         */
        public Builder date(String defaultDate) {
            mFormat = new SimpleDateFormat("yyyy-MM-dd");
            mFormat.setLenient(false);
            if (isValidDate(defaultDate)) {
                mDefaultYear = Integer.valueOf(defaultDate.split("-")[0]);
                mDefaultMonth = Integer.valueOf(defaultDate.split("-")[1]);
                mDefaultDay = Integer.valueOf(defaultDate.split("-")[2]);
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
            this.confirmTextColor = color;
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
         * 年月日文字颜色
         *
         * @param flagColor
         * @return
         */
        public Builder flagColor(String flagColor) {
            this.mFlagColor = flagColor;
            return this;
        }

        /**
         * 年月日文字大小
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
         * 背景色
         *
         * @param background
         * @return
         */
        public Builder setBackground(String background) {
            this.mBackground = background;
            return this;
        }

        public DatePicker build() {
            DatePicker cityPicker = new DatePicker(this);
            return cityPicker;
        }

    }

    public void setDate(Date date) {
        mCalendar.setTime(date);
        if (mDefaultYear != -1) {
            mMNPYear.setCurrentNumber(mDefaultYear);
            mCurrentYear = mDefaultYear;
            mCalendar.set(Calendar.YEAR, mDefaultYear);
        } else {
            mMNPYear.setCurrentNumber(mCalendar.get(Calendar.YEAR));
            mCurrentYear = mCalendar.get(Calendar.YEAR);
        }
        if (mDefaultMonth != -1) {
            mMNPMonth.setCurrentNumber(mDefaultMonth);
            mCurrentMonth = mDefaultMonth;
            mCalendar.set(Calendar.MONTH, mDefaultMonth - 1);
        } else {
            mMNPMonth.setCurrentNumber(mCalendar.get(Calendar.MONTH) + 1);
            mCurrentMonth = mCalendar.get(Calendar.MONTH) + 1;
        }
        if (mDefaultDay != -1) {
            mMNDay.setCurrentNumber(mDefaultDay);
            mCurrentDay = mDefaultDay;
            mCalendar.set(Calendar.DAY_OF_MONTH, mDefaultDay);
        } else {
            mMNDay.setCurrentNumber(mCalendar.get(Calendar.DAY_OF_MONTH));
            mCurrentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        }
        mMNDay.setEndNumber(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
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
        if (picker == mMNPYear) {
            mCalendar.set(Calendar.YEAR, newVal);
            mMNDay.setEndNumber(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            mCurrentYear = newVal;
        } else if (picker == mMNPMonth) {
            mCalendar.set(Calendar.MONTH, newVal - 1);
            mMNDay.setEndNumber(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            mCurrentMonth = newVal;
        } else if (picker == mMNDay) {
            mCalendar.set(Calendar.DAY_OF_MONTH, newVal);
            mCurrentDay = newVal;
        }
    }

}
