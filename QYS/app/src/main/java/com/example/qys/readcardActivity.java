package com.example.qys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class readcardActivity extends Activity {
    private static TextView block_0_Data;
    // NFC parts
    private static NfcAdapter mAdapter;
    private static PendingIntent mPendingIntent;
    private static IntentFilter[] mFilters;
    private static String[][] mTechLists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.activity_readcard);

//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.activity_readcard);
//        mAdapter = NfcAdapter.getDefaultAdapter(this);
//        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
//        mFilters = new IntentFilter[] { ndef };
//        mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };
//        Intent intent = getIntent();
//        resolveIntent(intent);
    }
    void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareClassic mfc = MifareClassic.get(tagFromIntent);
            try {
                String metaInfo = "ic"+Coverter.getUid(intent)+"\n";
                String ic_code=Coverter.getUid(intent);
                StringBuilder stringBuilder=new StringBuilder();
                String first=ic_code.substring(2,4);
                String second=ic_code.substring(0,2);
                String third=ic_code.substring(6,8);
                String firty=ic_code.substring(4,6);
                stringBuilder.append(first);
                stringBuilder.append(second);
                stringBuilder.append(third);
                stringBuilder.append(firty);
                String unicode_oilname="";
                //txt_iccode.setText(stringBuilder);
                Toast.makeText(readcardActivity.this,stringBuilder,Toast.LENGTH_SHORT).show();
                mfc.connect();

            } catch (IOException e) {
            }
        }
    }
    String hexString="0123456789abcdef";
    //把16进制转换成汉字
    public String decode(String bytes)
    {
        ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/2);
//		bytes=bytes.substring(2);
        Log.d("bytes",bytes);
        for(int k=0;k<bytes.length();k+=2)
        {
            int hex_int=hexString.indexOf(bytes.charAt(k))<<4 | hexString.indexOf(bytes.charAt(k+1));
            Log.d("hex_int",String.valueOf(hex_int) );
            baos.write((hexString.indexOf(bytes.charAt(k))<<4 | hexString.indexOf(bytes.charAt(k+1))));

        }
        String bb="";
        try
        {
            bb=new String(baos.toByteArray(),"GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bb;
//		return new String(baos.toByteArray());
    }

    //把16进制转换成汉字
    public String decode_byte(byte[] bytes)
    {

        String bb="";
        try
        {
            bb=new String(bytes,"GBK");
            Log.d("bb",bb);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bb;
    }

    //把16进制数组转换成16进制的字符串
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
        resolveIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    //内容的显示与隐藏
//    public void visiable_show(byte btyes)
//    {
//        switch (btyes)
//        {
//            case 0x31:
//                str_weight1.setText("原发毛重");
//                str_weight2.setText("原发皮重");
//                str_weight3.setText("原发净重");
//                panel_all.setVisibility(View.VISIBLE);
//                panel_crtname.setVisibility(View.GONE);
//                panel_weight1.setVisibility(View.VISIBLE);
//                panel_weight2.setVisibility(View.VISIBLE);
//                panel_weight3.setVisibility(View.VISIBLE);
//                break;
//            case 0x32:
//                str_weight1.setText("仓  一  量");
//                str_weight2.setText("仓  二  量");
//                str_weight3.setText("仓  三  量");
//                panel_all.setVisibility(View.VISIBLE);
//                panel_crtname.setVisibility(View.VISIBLE);
//                panel_weight1.setVisibility(View.VISIBLE);
//                panel_weight2.setVisibility(View.VISIBLE);
//                panel_weight3.setVisibility(View.VISIBLE);
//                break;
//            case 0x33:
//                panel_all.setVisibility(View.VISIBLE);
//                panel_crtname.setVisibility(View.GONE);
//                panel_weight1.setVisibility(View.GONE);
//                panel_weight2.setVisibility(View.GONE);
//                panel_weight3.setVisibility(View.GONE);
//                break;
//            case 0x34:
//                panel_all.setVisibility(View.GONE);
//        }
//    }
}
