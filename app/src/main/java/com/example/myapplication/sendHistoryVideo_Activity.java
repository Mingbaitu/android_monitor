package com.example.myapplication;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class sendHistoryVideo_Activity extends AppCompatActivity {
    private SurfaceView mSurfaceview = null; //  SurfaceView对象：预览摄像头
    private SurfaceHolder mSurfaceHolder = null; // SurfaceHolder对象：(抽象接口)SurfaceView支持类
    private Camera mCamera = null; //  Camera对象，相机预览

    private int realtime_token = 0;
    /*定义按钮：获取视频、连接服务器、上传视频*/
    private Button takeVideo,connect_server,upload_video;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
