package com.example.qys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        TextView cph=(TextView)findViewById(R.id.txt_license);
        TextView oilname=(TextView)findViewById(R.id.oilname);
        TextView mz=(TextView)findViewById(R.id.mz);
        TextView pz=(TextView)findViewById(R.id.pz);
        TextView ghdw=(TextView)findViewById(R.id.ghdw);
        TextView shdw=(TextView)findViewById(R.id.shdw);
        TextView dh=(TextView)findViewById(R.id.dh);
        TextView lc=(TextView)findViewById(R.id.lc);
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        String name = bundle.getString("by_license_plate");
        String oilnamea=bundle.getString("by_oilname");
        String mza=bundle.getString("by_gross_weight");
        String pza=bundle.getString("by_tare_weight");
        String ghdwa=bundle.getString("by_SupplyCompany");
        String shdwa=bundle.getString("by_ReciverCompany");
        String dha=bundle.getString("by_driver_phone");
        String lca=bundle.getString("process_name");

        cph.setText(name);
        oilname.setText(oilnamea);
        mz.setText(mza);
        pz.setText(pza);
        ghdw.setText(ghdwa);
        shdw.setText(shdwa);
        dh.setText(dha);
        lc.setText(lca);
    }
}
