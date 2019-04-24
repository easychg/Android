package com.example.qys;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class DcarsActivity extends AppCompatActivity {
    //油品下的一次二次过磅之间的车辆列表
    private static final String TAG="DcarsActivity";
    List<CityItem> cityList;
    RelativeLayout itmel;
    GridView gridView;
    private BaseAdapter adapter;
    private List<Good> list;
    private ListView listView;
    String response;//油品
    String response2;//车辆
    private TextView price;
    private int num = 0;
    private String serviceUrl;
    private String yp="cl";
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                try
                {
                    JSONObject json=new JSONObject(response2);
                    String code = json.getString("code");
                    String msga = json.getString("msg");
                    if(code.equals("200")){
                        JSONArray data = json.getJSONArray("data");
                        list = new ArrayList<Good>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject value = data.getJSONObject(i);
                            //获取到title值
                            String title = value.getString("by_license_plate");
                            String by_code=value.getString("by_oilname");
                            // String title = value.optString("title");
                            list.add(new Good(i+"    "+by_code +"   "+title, false,by_code));
                        }

                        // 赋值
//                        for (int i = 0; i < 60; i++) {
//                            list.add(new Good(i + "", false));
//                        }
                        // 适配
                        adapter = new Adper(list, DcarsActivity.this);
                        listView.setAdapter(adapter);
                    }else{
                        //弹出服务器查询信息结果
                        Toast.makeText(DcarsActivity.this,msga,Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                    Toast.makeText(DcarsActivity.this,"catch错误！",Toast.LENGTH_SHORT).show();
                }
            }
            if(msg.what==1){
                try
                {
                    JSONObject json=new JSONObject(response);
                    String code = json.getString("code");
                    String msga = json.getString("msg");
                    if(code.equals("200")){
                        JSONArray data = json.getJSONArray("data");
                        cityList = new ArrayList<CityItem>();
                        CityItem item0 = new CityItem();
                        item0.setCityName("全部");
                        item0.setCityCode("0");
                        cityList.add(item0);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject value = data.getJSONObject(i);
                            //获取到title值
                            String title = value.getString("dt_downame");
                            CityItem item = new CityItem();
                            item.setCityName(title);
                            item.setCityCode(""+(i+1));
                            cityList.add(item);
                        }
                        setGridView();//绘制油品UI
                        //cityList.addAll(cityList);
                        //Toast.makeText(DcarsActivity.this,"叫车成功！",Toast.LENGTH_SHORT).show();
                    }else{
                        //弹出服务器查询信息结果
                        Toast.makeText(DcarsActivity.this,msga,Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                    adapter.notifyDataSetChanged();
                    price.setText("请重新选择！");
                    num=0;
                    Toast.makeText(DcarsActivity.this,"catch错误！",Toast.LENGTH_SHORT).show();
                }
            }
            if(msg.what==2){
                Toast.makeText(DcarsActivity.this,"请选择叫号车辆！",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcars);
        Properties proper = ProperTies.getProperties(getApplicationContext());
        serviceUrl = proper.getProperty("serverUrl");
        //LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService("layout_inflater");
        gridView = (GridView) findViewById(R.id.grid);
        listView = (ListView) findViewById(R.id.listveiw);// 价格
        //setData();
        //setGridView();
        new Thread() {
            @Override
            public void run() {
                //查询所有卸车油品
                response = GetPostUtil.sendPost(serviceUrl+"getYP", "yp=yp");
                // 发送消息通知UI线程更新UI组件
                handler.sendEmptyMessage(1);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                //查询所有一次二次过磅之间的车辆
                response2 = GetPostUtil.sendPost(serviceUrl+"getCL", "cl=cl");
                // 发送消息通知UI线程更新UI组件
                handler.sendEmptyMessage(0);
            }
        }.start();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                // 取得ViewHolder对象
//                ViewHolder2 viewHolder2 = (ViewHolder2) arg1.getTag();
//                viewHolder2.textView.getText();
//                Log.d(TAG, "arg0: "+arg0);
                //Log.d(TAG, "arg1: "+viewHolder2.textView.getText());
//                Log.d(TAG, "arg2: "+cityList.get(arg2).getCityName());
//                Log.d(TAG, "arg3: "+arg3);
                yp=cityList.get(arg2).getCityName();
                if(yp=="全部") yp="cl";
                new Thread() {
                    @Override
                    public void run() {
                        //查询所有一次二次过磅之间的车辆
                        response2 = GetPostUtil.sendPost(serviceUrl+"getCL", "cl="+yp);
                        // 发送消息通知UI线程更新UI组件
                        handler.sendEmptyMessage(0);
                    }
                }.start();
            }
        });
    }
    /**设置GirdView参数，绑定数据*/
    private void setGridView() {
        int size = cityList.size();
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(5); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数

        GridViewAdapter adapter = new GridViewAdapter(getApplicationContext(),
                cityList);
        gridView.setAdapter(adapter);
    }

    /**GirdView 数据适配器*/
    public class GridViewAdapter extends BaseAdapter {
        Context context;
        List<CityItem> list;
        public GridViewAdapter(Context _context, List<CityItem> _list) {
            this.list = _list;
            this.context = _context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            TextView tvCity = (TextView) convertView.findViewById(R.id.tvCity);
            TextView tvCode = (TextView) convertView.findViewById(R.id.tvCode);
            CityItem city = list.get(position);

            tvCity.setText(city.getCityName());
            tvCode.setText(city.getCityCode());
            return convertView;
        }
    }
    public class CityItem {
        private String cityName;
        private String cityCode;
        public String getCityName() {
            return cityName;
        }
        public void setCityName(String cityName) {
            this.cityName = cityName;
        }
        public String getCityCode() {
            return cityCode;
        }
        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }
    }
}
