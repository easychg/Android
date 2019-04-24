package com.example.qys;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DownActivity extends Activity implements AdapterView.OnItemClickListener{
    //油品信息
    private ListView listview;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> data;
    private String serviceUrl;
    String response;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                try
                {
                    data=new ArrayList<Map<String,Object>>();
                    JSONObject json=new JSONObject(response);
                    String code = json.getString("code");
                    String msga = json.getString("msg");
                    if(code.equals("200")){
                        JSONArray jsona = json.getJSONArray("data");
                        for (int i = 0; i < jsona.length(); i++) {
                            JSONObject value = jsona.getJSONObject(i);
                            String title = value.getString("dt_downame");
                            Map<String, Object> map=new HashMap<String, Object>();
                            map.put("name", title);
                            data.add(map);
                        }
                        //simpleAdapter.notifyDataSetChanged();
                        //这里使用当前的布局资源作为ListView的模板。
                        //使用这种方式，SimpleAdapter会忽略ListView控件，仅以ListView之外的控件作为模板。
                        simpleAdapter = new SimpleAdapter(DownActivity.this, data,
                                R.layout.activity_down, new String[] {"name" }, new int[] {  R.id.oilname });
                        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        listview.setAdapter(simpleAdapter);

                    }else{
                        //弹出服务器查询信息结果
                        Toast.makeText(DownActivity.this,msga,Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                    Toast.makeText(DownActivity.this,"catch错误！",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down);
        Properties proper = ProperTies.getProperties(getApplicationContext());
        serviceUrl = proper.getProperty("serverUrl");
        listview = (ListView) findViewById(R.id.lvArray);
        //填充数据
        //putData();
        new Thread() {
            @Override
            public void run() {
                response = GetPostUtil.sendPost(serviceUrl+"getYP", "yp=yp");
                // 发送消息通知UI线程更新UI组件
                handler.sendEmptyMessage(0x123);
            }
        }.start();
//        //这里使用当前的布局资源作为ListView的模板。
//        //使用这种方式，SimpleAdapter会忽略ListView控件，仅以ListView之外的控件作为模板。
//        simpleAdapter = new SimpleAdapter(DownActivity.this, data,
//                R.layout.activity_down, new String[] {"name" }, new int[] {  R.id.oilname });
//        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        listview.setAdapter(simpleAdapter);
//        listview.setOnItemClickListener(this);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //通过view获取其内部的组件，进而进行操作
        String text = (String) ((TextView)view.findViewById(R.id.oilname)).getText();
        //大多数情况下，position和id相同，并且都从0开始
//        String showText = "点击第" + position + "项，文本内容为：" + text + "，ID为：" + id;
////        Toast.makeText(this, showText, Toast.LENGTH_LONG).show();
        Intent intent =new Intent(DownActivity.this,DcarActivity.class);
        //用Bundle携带数据
        Bundle bundle=new Bundle();
        bundle.putString("oilname", text);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
