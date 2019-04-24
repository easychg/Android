package com.example.testhttp;


import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    Button get, post;
    TextView show;
    // 代表服务器响应的字符串
    String response;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                // 设置show控件服务器响应
                show.setText(response);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        get = (Button) findViewById(R.id.get);
        post = (Button) findViewById(R.id.post);
        show = (TextView) findViewById(R.id.show);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: aa");
                        response = GetPostUtil.sendGet("http://api.map.baidu.com/telematics/v3/weather?location=嘉兴&output=json&ak=5slgyqGDENN7Sy7pw29IUvrZ", null);
                        try
                        {
                            JSONObject json=new JSONObject(response);
                            String id = json.getString("status");
                            String name = json.getString("message");
                            System.out.println("status" + id + ";message" + name );
                            Log.d(TAG, "status" + id + ";message" + name );
//                            JSONArray jsonArray = new JSONArray(response);
//                            for (int i=0; i < jsonArray.length(); i++)    {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        // 发送消息通知UI线程更新UI组件
                        handler.sendEmptyMessage(0x123);
                    }
                }.start();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        response = GetPostUtil.sendPost("http://192.168.1.134:8003/WebService1.asmx/getbyCPH", "chepaihao=鲁LD5109");
                    }
                }.start();
                // 发送消息通知UI线程更新UI组件
                handler.sendEmptyMessage(0x123);
            }
        });
    }
}
