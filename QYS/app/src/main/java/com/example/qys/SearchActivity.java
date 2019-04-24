package com.example.qys;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Properties;

public class SearchActivity extends AppCompatActivity {
    private EditText et_cph;
    private static final String TAG="SearchActivity";
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
                    Log.d(TAG, "handleMessage: "+code);
                    if(code.equals("200")){
                        //新建一个显式意图，第一个参数为当前Activity类对象，第二个参数为你要打开的Activity类
                        Intent intent =new Intent(SearchActivity.this,IndexActivity.class);
                        //用Bundle携带数据
                        Bundle bundle=new Bundle();
                        bundle.putString("by_license_plate", json.getString("by_license_plate"));
                        bundle.putString("by_oilname", json.getString("by_oilname"));
                        bundle.putString("by_gross_weight", json.getString("by_gross_weight"));
                        bundle.putString("by_tare_weight", json.getString("by_tare_weight"));
                        bundle.putString("by_SupplyCompany", json.getString("by_SupplyCompany"));
                        bundle.putString("by_ReciverCompany", json.getString("by_ReciverCompany"));
                        bundle.putString("by_driver_phone", json.getString("by_driver_phone"));
                        bundle.putString("process_name", json.getString("process_name"));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else{
                        //弹出服务器查询信息结果
                        Toast.makeText(SearchActivity.this,msga,Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                    Toast.makeText(SearchActivity.this,"catch错误！",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Properties proper = ProperTies.getProperties(getApplicationContext());
        serviceUrl = proper.getProperty("serverUrl");
        Button bt_search = (Button) findViewById(R.id.bt_search);
        et_cph = (EditText) findViewById(R.id.et_cph);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的数据
                String cph = et_cph.getText().toString();
                if(cph.isEmpty()){
                    Toast.makeText(SearchActivity.this,"车牌号不能为空！",Toast.LENGTH_SHORT).show();
                }else {
                    new Thread() {
                        @Override
                        public void run() {
                            response = GetPostUtil.sendPost(serviceUrl+"getbyCPH", "chepaihao="+et_cph.getText().toString());
                            // 发送消息通知UI线程更新UI组件
                            handler.sendEmptyMessage(0x123);
                        }
                    }.start();
                }
            }
        });
        Button bt_jiaohao = (Button) findViewById(R.id.bt_jiaohao);
        bt_jiaohao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this,DownActivity.class));
            }
        });
        Button bt_dqxc = (Button) findViewById(R.id.bt_dqxc);
        bt_dqxc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this,DcarsActivity.class));
            }
        });
    }
}
