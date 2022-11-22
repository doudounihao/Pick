package com.app.qqwpick.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.qqwpick.R;
import com.app.qqwpick.util.DateUtils;


public class AppletDeliveryStatusLayout extends ConstraintLayout {

    private ConstraintLayout main;
    private ImageView tips;
    private View view;
    private TextView time;
    private ImageView unit;

    public AppletDeliveryStatusLayout(Context context) {
        super(context);
    }

    public AppletDeliveryStatusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.delivery_status_layout, this);

        tips = findViewById(R.id.tips);
        view = findViewById(R.id.view);
        time = findViewById(R.id.time);
        unit = findViewById(R.id.unit);
        main = findViewById(R.id.main);
    }

    public void setData(String deadLine, String deliveryTime, String timeNow) {

        int min = 0;

        if (deadLine == null) {
            min = calculateTime(deliveryTime, timeNow);
        } else {
            min = calculateTime(deadLine, timeNow);
        }


        if (min >= 0) {//剩 余送 达
//            if (deadLine==null){
            tips.setImageResource(R.mipmap.icon_remaining_list);
            main.setBackgroundResource(R.mipmap.bg_remaining_list);
            unit.setImageResource(R.mipmap.icon_minute_remaining_list);
//            }else {
//                tips.setText("剩 余\n拣 货");
//                main.setBackgroundResource(R.drawable.bg_delivery_status_four);
//            }

            time.setText(String.valueOf(min));

            tips.setVisibility(VISIBLE);
            view.setVisibility(VISIBLE);
            unit.setVisibility(VISIBLE);
            time.setVisibility(VISIBLE);
        } else if (min > -60) {//已经超时
            tips.setImageResource(R.mipmap.icon_overtime_list);
            main.setBackgroundResource(R.mipmap.bg_overtime_list);
            unit.setImageResource(R.mipmap.icon_minute_overtime_list);
            time.setText(String.valueOf(Math.abs(min)));

            tips.setVisibility(VISIBLE);
            view.setVisibility(VISIBLE);
            unit.setVisibility(VISIBLE);
            time.setVisibility(VISIBLE);
        } else {
            main.setBackgroundResource(R.mipmap.icon_severe_list);
            time.setText("严重超时");

            time.setText("100");
            time.setVisibility(INVISIBLE);
            tips.setVisibility(INVISIBLE);
            view.setVisibility(INVISIBLE);
            unit.setVisibility(INVISIBLE);
        }
    }

    private int calculateTime(String deliveryTime, String timeNow) {
        Long aLong = DateUtils.computingTimeDifference(deliveryTime, timeNow);
        return DateUtils.longToMin(aLong);
    }
}