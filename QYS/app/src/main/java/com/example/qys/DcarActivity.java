package com.example.qys;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DcarActivity extends Activity {
    //油品下的车辆列表
    private static final String TAG="DcarActivity";
    private String serviceUrl;
    private Button button1;
    private Button button2;
    private Button button3;
    private ListView listView;
    private List<Good> list;
    private TextView price;
    private int num = 0;
    private int pric = 0;
    private BaseAdapter adapter;
    private Button button4;
    private List<Good> li;
    private String name;
    String response;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                try
                {
                    JSONObject json=new JSONObject(response);
                    String code = json.getString("code");
                    String msga = json.getString("msg");
                    if(code.equals("200")){
                        JSONArray data = json.getJSONArray("data");
                        list = new ArrayList<Good>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject value = data.getJSONObject(i);
                            //获取到title值
                            String title = value.getString("by_license_plate");
                            String by_code=value.getString("by_code");
                            // String title = value.optString("title");
                            list.add(new Good(i +"  "+ name+"  "+title, false,by_code));
                        }

                        // 赋值
//                        for (int i = 0; i < 60; i++) {
//                            list.add(new Good(i + "", false));
//                        }
                        // 适配
                        adapter = new Adper(list, DcarActivity.this);
                        listView.setAdapter(adapter);
                    }else{
                        //弹出服务器查询信息结果
                        Toast.makeText(DcarActivity.this,msga,Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                    Toast.makeText(DcarActivity.this,"catch错误！",Toast.LENGTH_SHORT).show();
                }
            }
            if(msg.what==1){
                try
                {
                    JSONObject json=new JSONObject(response);
                    String code = json.getString("code");
                    String msga = json.getString("msg");
                    if(code.equals("200")){
                        adapter.notifyDataSetChanged();
                        num=0;
                        price.setText("一共选了0辆");
                        Toast.makeText(DcarActivity.this,"叫车成功！",Toast.LENGTH_SHORT).show();
                    }else{
                        //弹出服务器查询信息结果
                        Toast.makeText(DcarActivity.this,msga,Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                    adapter.notifyDataSetChanged();
                    price.setText("请重新选择！");
                    num=0;
                    Toast.makeText(DcarActivity.this,"catch错误！",Toast.LENGTH_SHORT).show();
                }
            }
            if(msg.what==2){
                Toast.makeText(DcarActivity.this,"请选择叫号车辆！",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcar);
        Properties proper = ProperTies.getProperties(getApplicationContext());
        serviceUrl = proper.getProperty("serverUrl");
        button1 = (Button) findViewById(R.id.button1);// 全选
        button2 = (Button) findViewById(R.id.button2);// 反选
        button3 = (Button) findViewById(R.id.button3);// 全不选
        button4 = (Button) findViewById(R.id.button4);// 叫车
        listView = (ListView) findViewById(R.id.listveiw);// 价格

        price = (TextView) findViewById(R.id.price);
//        list = new ArrayList<Good>();
//        // 赋值
//        for (int i = 0; i < 60; i++) {
//            list.add(new Good(i + "", false));
//        }
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        name = bundle.getString("oilname");
        new Thread() {
            @Override
            public void run() {
                response = GetPostUtil.sendPost(serviceUrl+"getbyYP", "youpin="+name);
                // 发送消息通知UI线程更新UI组件
                handler.sendEmptyMessage(0);
            }
        }.start();
//        // 适配
//        adapter = new Adper(list, DcarActivity.this);
//        listView.setAdapter(adapter);
        // 全选
        button1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                num = 0;
                pric = 0;

                for (int i = 0; i < list.size(); i++) {
                    // 改变boolean
                    list.get(i).setBo(true);
                    // 如果为选中
                    if (list.get(i).getBo()) {

                        num++;
                        //pric += Integer.parseInt(list.get(i).getName());

                    }
                }
                // 刷新
                adapter.notifyDataSetChanged();
                // 显示
                price.setText("一共选了" + num + "辆");

            }
        });
        // 反选
        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                num = 0;
                pric = 0;
                for (int i = 0; i < list.size(); i++) {
                    // 改值
                    if (list.get(i).getBo()) {
                        list.get(i).setBo(false);
                    } else {
                        list.get(i).setBo(true);
                    }
                    // 刷新
                    adapter.notifyDataSetChanged();
                    // 如果为选中
                    if (list.get(i).getBo()) {

                        num++;
                        //pric += Integer.parseInt(list.get(i).getName());

                    }
                }
                // 用TextView显示
                price.setText("一共选了" + num + "辆");
            }
        });
        // 全不选
        button3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                num = 0;
                pric = 0;
                for (int i = 0; i < list.size(); i++) {
                    // 改值
                    list.get(i).setBo(false);
                    // 刷新
                    adapter.notifyDataSetChanged();
                    // 如果为选中
                    if (list.get(i).getBo()) {
                        num++;
                        //pric += Integer.parseInt(list.get(i).getName());
                    }

                }
                price.setText("一共选了" + num + "辆" );
            }
        });
        // 删除1
        /*
         * li = new ArrayList<Good>(); button4.setOnClickListener(new
         * OnClickListener() {
         *
         * public void onClick(View v) { // TODO Auto-generated method stub for
         * (int i = 0; i < list.size(); i++) { if(list.get(i).getBo()){
         * //把要删除的保存起来 li.add(list.get(i));
         *
         * }
         *
         * } //删除 list.removeAll(li); adapter.notifyDataSetChanged(); num = 0;
         * pric = 0; price.setText("一共选了"+num+"件,"+"价格是"+pric+"元"); } });
         */
        // 删除2
        /*button4.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getBo()) {
                        list.remove(i);
                       i--;
                    }

                }

                adapter.notifyDataSetChanged();
                num = 0;
                pric = 0;
                price.setText("一共选了" + num + "件," + "价格是" + pric + "元");
            }
        });*/
        // 叫号
        button4.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 获取list集合对应的迭代器

                new Thread() {
                    @Override
                    public void run() {
                        Iterator it = list.iterator();
                        String codes="0";
                        while (it.hasNext()) {
                            // 得到对应集合元素
                            Good g = (Good) it.next();
                            // 判断
                            if (g.getBo()) {
                                // 从集合中删除上一次next方法返回的元素
                                codes+=","+g.getBy_code();
                                it.remove();
                            }
                        }
                        if(codes.length()>1){
                            Log.d(TAG, "codes: "+codes);
                            response = GetPostUtil.sendPost(serviceUrl+"JHD", "codes="+codes);
                            // 发送消息通知UI线程更新UI组件
                            handler.sendEmptyMessage(1);
                        }else {
                            handler.sendEmptyMessage(2);

                        }

                    }
                }.start();
                // 刷新
//                adapter.notifyDataSetChanged();
//                num = 0;
//                pric = 0;
//                // 显示
//                price.setText("一共选了" + num + "辆"+codes);
            }
        });
        // 绑定listView的监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                // 取得ViewHolder对象
                Adper.ViewHolder viewHolder = (Adper.ViewHolder) arg1.getTag();
                // 改变CheckBox的状态
                viewHolder.checkBox.toggle();
                // 将CheckBox的选中状况记录下来
                list.get(arg2).setBo(viewHolder.checkBox.isChecked());
                // 调整选定条目
                if (viewHolder.checkBox.isChecked() == true) {
                    num++;
                    //pric += Integer.parseInt(list.get(arg2).getName());
                } else {
                    num--;
                    //pric -= Integer.parseInt(list.get(arg2).getName());
                }
                // 用TextView显示
                price.setText("一共选了" + num + "辆" );
            }
        });
    }

}
