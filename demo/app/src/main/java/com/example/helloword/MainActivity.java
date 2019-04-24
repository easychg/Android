package com.example.helloword;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private static final int CHANGE_UI=1;
    private static final int ERROR=2;
    private EditText et_path;
    private ImageView iv;

    //主线程创建消息处理器
    private Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.what==CHANGE_UI) {
                Bitmap bitmap= (Bitmap) msg.obj;
                iv.setImageBitmap(bitmap);
            }else if (msg.what==ERROR) {
                Toast.makeText(MainActivity.this, "访问失败", Toast.LENGTH_SHORT).show();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_path=(EditText) findViewById(R.id.et_path);
        iv=(ImageView) findViewById(R.id.iv);
        new Thread(){
            private HttpURLConnection conn;
            private Bitmap bitmap;
            @Override
            public void run() {
                // 链接服务器 get 请求，获取图片
                //在URL的构造方法中传入要访问的资源路径
                URL url;
                try {
                    url = new URL("http://pic1.cxtuku.com/00/06/78/b9903ad9ea2b.jpg");
                    conn = (HttpURLConnection) url.openConnection();
                    //设置请求方式，在这里设置的事GET方式
                    conn.setRequestMethod("GET");
                    //设置请求超时时间，单位是毫秒(mm)
                    conn.setConnectTimeout(5000);
                    //得到服务器返回的响应码
                    int code = conn.getResponseCode();
                    //  请求网络成功后返回200
                    if (code==200) {
                        //获取服务器返回的输入流
                        InputStream is=conn.getInputStream();
                        //将流转换成Bitmap对象
                        bitmap= BitmapFactory.decodeStream(is);
                        Message msg=new Message();
                        msg.what=CHANGE_UI;
                        msg.obj=bitmap;

                    }else {
                        //返回码不是200，请求服务器失败
                        Message msg=new Message();
                        msg.what=ERROR;

                    }
                    //关闭Http链接
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message msg=new Message();
                msg.what=ERROR;

            }
        }.start();
    }
    public static void click(final MainActivity mainActivity, View view) {
        final String path= mainActivity.et_path.getText().toString().trim();
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(mainActivity, "图片路径不能为空", Toast.LENGTH_SHORT).show();
        }else {
            //子线程请求网络，Android 4.0以后访问网络不能放在主线程中

        }
    }
}
