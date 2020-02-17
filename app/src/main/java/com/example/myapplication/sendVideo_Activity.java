package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hb.dialog.myDialog.ActionSheetDialog;

import java.io.File;

public class sendVideo_Activity extends AppCompatActivity {
    private Camera mCamera = null; //  Camera对象，相机预览
    private VideoView pre_video = null;

    public static final int CHOOSE_FROM_ALBUM = 2;

    private String videoPath = null;

    /*定义按钮：获取视频、连接服务器、上传视频*/
    private Button takeVideo,connect_server,upload_video;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_history_video);

        //初始化
        takeVideo = findViewById(R.id.take_video);
        connect_server = findViewById(R.id.connect_server);
        upload_video = findViewById(R.id.upload_video);
        pre_video = findViewById(R.id.preview_history_video);

        //获取视频按钮
        takeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置底部下拉框进行选择
                ActionSheetDialog dialog = new ActionSheetDialog(sendVideo_Activity.this)
                        .builder()
                        .setTitle("请选择")
                        .addSheetItem("实时拍摄", null, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int i) {
                                Intent intent = new Intent(sendVideo_Activity.this,sendRealtimeVideo_Activity.class);
                                startActivity(intent);
                            }
                        }).addSheetItem("从相册中打开", null, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int i) {
                                /*从相册打开视频*/
                                //1.首先动态获取权限
                                if(ContextCompat.checkSelfPermission(sendVideo_Activity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(sendVideo_Activity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,1);
                                }else{
                                    openAlbum();
                                }


                            }
                        });
                dialog.show();

            }
        });


        connect_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(sendVideo_Activity.this,"请先选择视频！！！",Toast.LENGTH_SHORT).show();
            }
        });


        upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(sendVideo_Activity.this,"请先选择视频！！！",Toast.LENGTH_SHORT).show();
            }
        });

    }


    /*
       openAlbum:打开相册函数
     */
    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,CHOOSE_FROM_ALBUM);
    }

    /* onRequestPermissionsResult:请求权限许可函数*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    /*onActivityResult : 响应函数，对应于startActivityForResult*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOOSE_FROM_ALBUM:
                if(resultCode == Activity.RESULT_OK){
                    try{
                        if(data != null){
                            Uri selectedVideo = data.getData();
                            /*
                            String[] filePathColumn = {MediaStore.Video.Media.DATA};
                            Cursor cursor = getContentResolver().query(selectedVideo,
                                    filePathColumn,null,null,null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            videoPath = cursor.getString(columnIndex);   //获取视频存储地址
                            cursor.close();
                            //submit_vd_ad.setText(VIDEOPATH);

                             */
                            //获取路径
                            String[] filePathColumn = {MediaStore.Video.Media.DATA};
                            Cursor cursor = getContentResolver().query(selectedVideo,filePathColumn,
                                    null,null,null);
                            if(cursor.moveToFirst()){
                                videoPath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                                Toast.makeText(sendVideo_Activity.this,videoPath ,Toast.LENGTH_SHORT).show();
                            }
                            cursor.close();
                            playVideo();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }catch (OutOfMemoryError oe){
                        oe.printStackTrace();
                    }
                }
        }
    }

    /*
    *   playVideo:播放视频
    * */

    private void playVideo(){
        //if(videoPath != null){
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/video.mp4");
            Uri uri = Uri.fromFile(file);
            Toast.makeText(this,"have found video",Toast.LENGTH_SHORT).show();
            pre_video.setVideoURI(uri);
            pre_video.setVideoPath(file.getAbsolutePath());
            MediaController mediaController = new MediaController(this);
            pre_video.setMediaController(mediaController);
            mediaController.setMediaPlayer(pre_video);
            pre_video.requestFocus();
            pre_video.start();
            /*
        }else{
            Toast.makeText(sendVideo_Activity.this,"视频路径为空！",Toast.LENGTH_SHORT).show();
        }

             */

    }
}
