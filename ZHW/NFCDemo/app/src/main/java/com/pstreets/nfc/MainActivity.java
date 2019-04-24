package com.pstreets.nfc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	// UI Elements
	private static TextView block_0_Data;
	private TextView txt_iccode;
	private TextView txt_license;
	private TextView txt_type;
	private TextView txt_oilname;
	private TextView txt_crtname;
	private TextView txt_weight1,str_weight1,str_weight2,str_weight3;
	private TextView txt_weight2;
	private TextView txt_weight3;
	private LinearLayout panel_crtname;
	private LinearLayout panel_weight1;
	private LinearLayout panel_weight2;
	private LinearLayout panel_weight3;
	private LinearLayout panel_all;
	// NFC parts
	private static NfcAdapter mAdapter;
	private static PendingIntent mPendingIntent;
	private static IntentFilter[] mFilters;
	private static String[][] mTechLists;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.main);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		block_0_Data = (TextView) findViewById(R.id.promt);
		txt_iccode=(TextView)findViewById(R.id.txt_iccode);
		txt_license=(TextView)findViewById(R.id.txt_license);
		txt_type=(TextView)findViewById(R.id.txt_type);
		txt_oilname=(TextView)findViewById(R.id.txt_oilname);
		txt_crtname=(TextView)findViewById(R.id.txt_crtname);
		txt_weight1=(TextView)findViewById(R.id.txt_weight1);
		txt_weight2=(TextView)findViewById(R.id.txt_weight2);
		txt_weight3=(TextView)findViewById(R.id.txt_weight3);
		str_weight1=(TextView)findViewById(R.id.str_weight1);
		str_weight2=(TextView)findViewById(R.id.str_weight2);
		str_weight3=(TextView)findViewById(R.id.str_weight3);
		panel_crtname=(LinearLayout)findViewById(R.id.panel_crtname);
		panel_weight1=(LinearLayout)findViewById(R.id.panel_weight1);
		panel_weight2=(LinearLayout)findViewById(R.id.panel_weight2);
		panel_weight3=(LinearLayout)findViewById(R.id.panel_weight3);
		panel_all=(LinearLayout)findViewById(R.id.panel_all);

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
//		try {
//			ndef.addDataType("*/*");
//		} catch (MalformedMimeTypeException e) {
//			throw new RuntimeException("fail", e);
//		}
		mFilters = new IntentFilter[] { ndef };
		mTechLists = new String[][] { new String[] { MifareClassic.class
				.getName() } };

		Intent intent = getIntent();
		resolveIntent(intent);
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
				txt_iccode.setText(stringBuilder);
				mfc.connect();
				boolean auth = false;
				int secCount = mfc.getSectorCount();
				for (int j = 0; j < secCount; j++) {
					auth = mfc.authenticateSectorWithKeyA(j,MifareClassic.KEY_DEFAULT);
					int bCount = 0;
					int bIndex = 0;
					if (auth) {
						bCount = mfc.getBlockCountInSector(j);
						bIndex = mfc.sectorToBlock(j);
						for (int i = 0; i < bCount; i++) {
							byte []data = mfc.readBlock(bIndex);
							metaInfo += "Block " + bIndex + " : "+ bytesToHexString(data) + "\n";
							if(bIndex==1)
							{//类型
								String type_receive=bytesToHexString(data).trim().substring(2,2);
								String type_str="";
								visiable_show(data[0]);
								if(data[0]==0x31)
								{
									type_str="卸车";

								}
								else if(data[0]==0x32)
								{
									type_str="装车";
								}
								else {
									if (data[0] == 0x33) {
										type_str = "零散";
									} else {
										data[0] = 0x34;
										visiable_show(data[0]);
//										Toast.makeText(MainActivity.this, "该卡号没有数据", Toast.LENGTH_LONG).show();
										Dialog alertDialog=new AlertDialog.Builder(this).setTitle("提示").
												setMessage("该卡号没有数据").setIcon(R.drawable.icon)
												.setPositiveButton("确定",new DialogInterface.OnClickListener(){
													public void onClick(DialogInterface dialogInterface, int i) {

													}
												})
												.create();
										alertDialog.show();
										return;
									}
								}
								txt_type.setText(type_str);
							}
							if(bIndex==5) {//车牌号
								String str_hex = bytesToHexString(data).trim();
								String hz=decode(str_hex).substring(1);

								txt_license.setText(hz);
							}
							if(bIndex==9) {//物料名称
								String str_hex = bytesToHexString(data).trim();
								String hz=decode(str_hex).substring(1);
								unicode_oilname=hz;
								txt_oilname.setText(unicode_oilname);
							}
//							if(bIndex==10)
//							{//原因是当物料名称过长时会自动写入到第10块
//								String str_hex = bytesToHexString(data).trim();
//								String hz=decode(str_hex);
//								unicode_oilname+=hz;
//								txt_oilname.setText(unicode_oilname);
//							}
							if(bIndex==13) {//鹤位
								String str_hex = bytesToHexString(data).trim();
								String hz=decode(str_hex).substring(1);

								txt_crtname.setText(hz);
							}
							if(bIndex==17) {//重量一
								String str_hex = bytesToHexString(data).trim();
								String hz=decode(str_hex).substring(1);

								txt_weight1.setText(hz+" 吨");
							}
							if(bIndex==21) {//重量二
								String str_hex = bytesToHexString(data).trim();
								String hz=decode(str_hex).substring(1);

								txt_weight2.setText(hz+" 吨");
							}
							if(bIndex==25) {//重量三
								String str_hex = bytesToHexString(data).trim();
								String hz=decode(str_hex).substring(1);

								txt_weight3.setText(hz+" 吨");
							}
							bIndex++;
						}
					} else { 
						 metaInfo += "Sector " + j + "\n";
					}
				}
				block_0_Data.setText(metaInfo);
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
	public void visiable_show(byte btyes)
	{
		switch (btyes)
		{
			case 0x31:
				str_weight1.setText("原发毛重");
				str_weight2.setText("原发皮重");
				str_weight3.setText("原发净重");
				panel_all.setVisibility(View.VISIBLE);
				panel_crtname.setVisibility(View.GONE);
				panel_weight1.setVisibility(View.VISIBLE);
				panel_weight2.setVisibility(View.VISIBLE);
				panel_weight3.setVisibility(View.VISIBLE);
				break;
			case 0x32:
				str_weight1.setText("仓  一  量");
				str_weight2.setText("仓  二  量");
				str_weight3.setText("仓  三  量");
				panel_all.setVisibility(View.VISIBLE);
				panel_crtname.setVisibility(View.VISIBLE);
				panel_weight1.setVisibility(View.VISIBLE);
				panel_weight2.setVisibility(View.VISIBLE);
				panel_weight3.setVisibility(View.VISIBLE);
				break;
			case 0x33:
				panel_all.setVisibility(View.VISIBLE);
				panel_crtname.setVisibility(View.GONE);
				panel_weight1.setVisibility(View.GONE);
				panel_weight2.setVisibility(View.GONE);
				panel_weight3.setVisibility(View.GONE);
				break;
			case 0x34:
				panel_all.setVisibility(View.GONE);
		}
	}


}