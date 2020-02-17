package com.example.myapplication.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;


public class Palace extends BaseAdapter {
    String[] name;//定义一个接受构造传入的应用名
    int[] icon; //定义一个接受构造传入的图片ID数组
    Context context; //接受上下文
    public Palace(Context context,String[] name,int[] icon){
        super();
        this.name = name;
        this.icon = icon;
        this.context = context;
    }
    /*返回条目的总数*/
    @Override
    public int getCount() {
        return icon.length;
    }
    /*返回指定下标对应的数据对象*/
    @Override
    public Object getItem(int position) {
        return icon[position];
    }
    /*返回每个条目的ID*/
    @Override
    public long getItemId(int position) {
        return position;
    }
    /*返回指定下标对应的Item的view对象*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        pag pbg = null;
        if(convertView == null){
            //若显示条目为空则赋值，不如一直赋值会导致内存溢出
            pbg = new pag();
            //加载子布局赋值到convertView中
            convertView = View.inflate(context, R.layout.camera_choose_item,null);
            pbg.imageView = convertView.findViewById(R.id.iv_apply_image);
            pbg.textView = convertView.findViewById(R.id.iv_apply_name);
            convertView.setTag(pbg);
        }else{
            //若加载的条目显示有值
            pbg = (pag)convertView.getTag();
        }
        //给控件赋值
        pbg.imageView.setImageResource(icon[position]);
        pbg.textView.setText(name[position]);
        return convertView;
    }
    //定义数据包类
    class pag{
        ImageView imageView;
        TextView textView;
    }
}
