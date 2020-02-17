package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.utils.Camera;
import com.example.myapplication.utils.Palace;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

public class cameraChoose_Activity  extends AppCompatActivity {

    private TitleBar myTitleBar;
    private GridView gridView;
    private Palace palace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_choose);

        myTitleBar = findViewById(R.id.title1);
        myTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            Toast toast;
            @Override
            public void onLeftClick(View v) {
                Intent intent = new Intent(cameraChoose_Activity.this,MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {
            }
        });

        //接收相关参数：摄像头名称、摄像头图标
        Intent intent = getIntent();
        Camera rec_camera = (Camera)getIntent().getSerializableExtra("rec_camera");
        //下一步跳转页面的类型
        final String page_type = intent.getStringExtra("页面");

        final String[] camera_name = rec_camera.getName();   //摄像头名称
        int[] camera_icon = rec_camera.getIcon();     //摄像头图标

        gridView = findViewById(R.id.camera_list);
        //新建自定义适配器
        palace = new Palace(this,camera_name,camera_icon);
        //加载适配器
        gridView.setAdapter(palace);
        //加载点击事件监听器
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(cameraChoose_Activity.this,camera_name[position],Toast.LENGTH_SHORT).show();
                //设置对摄像头的点击事件,跳转至摄像头页面，设置获取该摄像头对应的图像（日期）/实时视频的页面
                //选取摄像头对应的用户编号为11111
                if(page_type.equals("video")){
                    //跳转至接收视频页面
                }else if(page_type.equals("pic")){
                    //跳转至接收图像页面

                }
            }
        });

    }
}
