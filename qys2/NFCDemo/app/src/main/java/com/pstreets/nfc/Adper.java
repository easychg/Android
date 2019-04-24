package com.pstreets.nfc;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class Adper extends BaseAdapter {
    List<Good> list;
    Context context;

    public Adper(List<Good> list, Context context) {
        // TODO Auto-generated constructor stub
        this.list=list;
        this.context=context;


    }

    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder viewHolder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.listveiw, null);
            viewHolder=new ViewHolder();
            viewHolder.textView=(TextView) convertView.findViewById(R.id.text);
            viewHolder.txtby_code=(TextView) convertView.findViewById(R.id.by_code);
            viewHolder.checkBox=(CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText("车辆信息："+list.get(position).getName());
        viewHolder.txtby_code.setText(list.get(position).getBy_code());
        //显示checkBox
        viewHolder.checkBox.setChecked(list.get(position).getBo());

        return convertView;

    }
    class ViewHolder{
        TextView textView;
        TextView txtby_code;
        CheckBox checkBox;
    }
}
