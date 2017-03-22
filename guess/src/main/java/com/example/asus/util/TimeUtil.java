package com.example.asus.util;

import android.os.Handler;
import android.widget.Button;

import com.example.asus.activity.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yhao on 2016/12/28.
 * use:  new TimeUtil(mSendButton, mSendButton.getText().toString()).RunTimer();
 *
 *   按钮60s不可点击
 */

public class TimeUtil {
    private int time = 60;
    private Timer timer;
    private Button btnSure;
    private String btnText;

    public TimeUtil(Button btnSure, String btnText) {
        this.btnSure = btnSure;
        this.btnText = btnText;
    }


    public void RunTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                time--;
                handler.sendEmptyMessage(1);
            }
        };
        timer.schedule(task, 100, 1000);
    }


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (time > 0) {
                    btnSure.setEnabled(false);
                    btnSure.setBackgroundResource(R.drawable.orange_bt_shape);
                    btnSure.setText(time + "秒后可重新发送");
                } else {
                    timer.cancel();
                    btnSure.setBackgroundResource(R.drawable.green_bt_shape);
                    btnSure.setText(btnText);
                    btnSure.setEnabled(true);
                }
            }
        }
    };


}
