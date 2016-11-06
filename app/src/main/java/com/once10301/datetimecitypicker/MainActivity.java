package com.once10301.datetimecitypicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ly.datetimecitypicker.CityPicker;
import com.ly.datetimecitypicker.DatePicker;
import com.ly.datetimecitypicker.TimePicker;


public class MainActivity extends AppCompatActivity {
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private CityPicker mCityPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatePicker = new DatePicker.Builder(MainActivity.this)
                .date("2015-02-28")
                .confirmTextColor("#16b3f4")
                .cancelTextColor("#333333")
                .textColor("#16b3f4")
                .textSize(20)
                .flagColor("#16b3f4")
                .flagSize(20)
                .visibleItemsCount(7)
                .itemPadding(20)
                .isSound(true)
                .setBackground("#ffffff")
                .build();
        Button btnDate = (Button) findViewById(R.id.btnDate);
        final TextView tvDate = (TextView) findViewById(R.id.tvDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePicker.show();
                mDatePicker.setOnDateClickListener(new DatePicker.OnDateClickListener() {
                    @Override
                    public void onSelected(String... citySelected) {
                        tvDate.setText("选择结果：\n年：" + citySelected[0] + "\n月：" + citySelected[1] + "\n日：" + citySelected[2]);
                    }
                });
            }
        });

        mTimePicker = new TimePicker.Builder(MainActivity.this)
                .time("10:57")
                .confirmTextColor("#16b3f4")
                .cancelTextColor("#333333")
                .textColor("#16b3f4")
                .textSize(20)
                .flagColor("#16b3f4")
                .flagSize(20)
                .visibleItemsCount(7)
                .itemPadding(20)
                .isSound(true)
                .setBackground("#ffffff")
                .build();
        Button btnTime = (Button) findViewById(R.id.btnTime);
        final TextView tvTime = (TextView) findViewById(R.id.tvTime);
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker.show();
                mTimePicker.setOnTimeClickListener(new TimePicker.OnTimeClickListener() {
                    @Override
                    public void onSelected(String... citySelected) {
                        tvTime.setText("选择结果：\n时：" + citySelected[0] + "\n分：" + citySelected[1]);
                    }
                });
            }
        });

        mCityPicker = new CityPicker.Builder(MainActivity.this)
                //   .province("湖北省")
                //  .city("黄冈市")
                //   .county("红安县")
                .confirmTextColor("#16b3f4")
                .cancelTextColor("#333333")
                .textColor("#16b3f4")
                .textSize(20)
                .visibleItemsCount(7)
                .itemPadding(20)
                .isSound(true)
                .setBackground("#ffffff")
                .build();
        Button btnCity = (Button) findViewById(R.id.btnCity);
        final TextView tvCity = (TextView) findViewById(R.id.tvCity);
        btnCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCityPicker.show();
                mCityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
                    @Override
                    public void onSelected(String... citySelected) {
                        tvCity.setText("选择结果：\n省：" + citySelected[0] + "\n市：" + citySelected[1] + "\n县：" + citySelected[2]
                                + "\nID：" + citySelected[3]);
                    }
                });
            }
        });
    }
}
