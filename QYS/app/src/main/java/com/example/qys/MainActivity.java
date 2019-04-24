package com.example.qys;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    private EditText et_username;
    private EditText et_password;
    private Button bt_log;
    private Button bt_bos;
    private String serviceUrl;
    String response;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {

                try
                {
                    JSONObject json=new JSONObject(response);
                    String code = json.getString("code");
                    String msga = json.getString("msg");
                    if(code.equals("200")){
                        Toast.makeText(MainActivity.this,"用户名和密码正确！",Toast.LENGTH_SHORT).show();
                        Integer time = 1000;    //设置等待时间，单位为毫秒
                        Handler handler = new Handler();
                        //当计时结束时，跳转至主界面
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this,SearchActivity.class));
                                MainActivity.this.finish();
                            }
                        }, time);
                    }else{
                        //弹出服务器查询信息结果
                        Toast.makeText(MainActivity.this,msga,Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                    Toast.makeText(MainActivity.this,"catch错误！",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //利用布局资源文件设置用户界面
        setContentView(R.layout.activity_main);
        Properties proper = ProperTies.getProperties(getApplicationContext());
        serviceUrl = proper.getProperty("serverUrl");
        //通过资源标识获得控件实例
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        bt_log = (Button) findViewById(R.id.bt_log);
        bt_bos = (Button) findViewById(R.id.bt_bos);

        //给登录按钮注册监听器，实现监听器接口，编写事件
        bt_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的数据
                String strUsername = et_username.getText().toString();
                String strPassword = et_password.getText().toString();
                if(strUsername.isEmpty()||strPassword.isEmpty()){
                    Toast.makeText(MainActivity.this,"用户名和密码不能为空！",Toast.LENGTH_SHORT).show();
                }else {
                    new Thread() {
                        @Override
                        public void run() {
                            response = GetPostUtil.sendPost(serviceUrl+"chekUser", "username="+et_username.getText().toString()+"&password="+et_password.getText().toString());
                            // 发送消息通知UI线程更新UI组件
                            handler.sendEmptyMessage(0x123);
                        }
                    }.start();
                }
            }
        });
        //给取消按钮注册监听器，实现监听器接口，编写事件
        bt_bos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
