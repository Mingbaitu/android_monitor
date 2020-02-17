package com.example.myapplication;

import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import com.example.myapplication.utils.SocketCommand;

/*
    获取视频并上传至服务器：实时拍摄视频/上传历史视频
 */public class sendRealtimeVideo_Activity extends AppCompatActivity implements SurfaceHolder.Callback,Camera.PreviewCallback{

    private SocketCommand socketCommand;    // 与服务器交互的命令类

    private SurfaceView mSurfaceview = null; //  SurfaceView对象：预览摄像头
    private SurfaceHolder mSurfaceHolder = null; // SurfaceHolder对象：(抽象接口)SurfaceView支持类
    private Camera mCamera = null; //  Camera对象，相机预览

    /*定义按钮：获取视频、连接服务器、上传视频*/
    private Button takeVideo,connect_server,upload_video;

    /**用户名*/
    private String pUsername="BAITU";
    /**服务器地址*/
    private String serverUrl="192.168.1.100";
    /*连接服务器的手机号*/
    private String phoneNumber = "13129956383";
    /*本机MAC地址*/
    private String MAC = MainActivity.getLocalMacAddressFromIp();
    /**服务器端口*/
    private int serverPort=8888;
    /**视频刷新间隔*/
    private int VideoPreRate=1;
    /**当前视频序号*/
    private int tempPreRate=0;
    /**视频质量*/
    private int VideoQuality=85;

    /**发送视频宽度比例*/
    private float VideoWidthRatio=1;
    /**发送视频高度比例*/
    private float VideoHeightRatio=1;

    /**发送视频宽度*/
    private int VideoWidth=320;
    /**发送视频高度*/
    private int VideoHeight=240;
    /**视频格式索引*/
    private int VideoFormatIndex=0;
    /**是否发送视频*/
    private boolean startSendVideo=false;
    /**是否连接主机*/
    private boolean connectedServer=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_realtime_video);

        //禁止屏幕休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //初始化
        socketCommand = new SocketCommand();
        mSurfaceview = findViewById(R.id.preview_video);
        connect_server = findViewById(R.id.connect_server);
        upload_video = findViewById(R.id.upload_video);


        //获取视频按钮
        /*
        takeVideo = findViewById(R.id.take_video);
        takeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置底部下拉框进行选择
                ActionSheetDialog dialog = new ActionSheetDialog(sendRealtimeVideo_Activity.this)
                        .builder()
                        .setTitle("请选择")
                        .addSheetItem("实时拍摄", null, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int i) {
                                Toast.makeText(sendRealtimeVideo_Activity.this,"当前已在实时拍摄界面!!!",Toast.LENGTH_SHORT).show();
                            }
                        }).addSheetItem("从相册中打开", null, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int i) {

                            }
                        });
                dialog.show();

            }
        });

         */

        //开始连接主机按钮
        connect_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectedServer){
                    //停止连接主机，同时断开传输
                    startSendVideo=false;
                    connectedServer=false;
                    upload_video.setEnabled(false);
                    connect_server.setText("开始连接");
                    upload_video.setText("开始传输");
                    //断开连接
                    //Thread th = new MySendCommandThread("PHONEDISCONNECT|"+pUsername+"|");  // 断开连接指令
                    Thread th = new MySendCommandThread(socketCommand.disconnectServer(pUsername));
                    th.start();
                }else{
                    //连接服务器
                    //String phoneconnect = "PHONECONNECT|"+pUsername+"|";
                    Thread th = new MySendCommandThread(socketCommand.connectServer(pUsername));
                    th.start();
                    connectedServer = true; //下一次按动按钮时即为关闭连接操作了
                    upload_video.setEnabled(true); //此时可以将上传实时视频打开
                    connect_server.setText("停止连接");
                }
            }
        });

        upload_video.setEnabled(false); //自然情况下应该关闭按钮局功能
        //开始上传实时视频按钮
        upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startSendVideo){
                    //此时的操作是停止传输视频
                    startSendVideo = false;
                    upload_video.setText("上传视频");
                }else{
                    //上传视频
                    startSendVideo = true;
                    upload_video.setText("停止上传");
                }
            }
        });
    }

    /*
       MySendCommondThread:发送命令线程
     */
    class MySendCommandThread extends Thread{
        //command:命令字符串
        private String command;
        public MySendCommandThread(String command){
            this.command = command;
        }
        public void run(){
            //实例化Socket
            try{
                Socket socket = new Socket(serverUrl,serverPort);//设置服务器IP、端口号
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(command);
                out.flush(); //清空输出流
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /*onStart()：重新启动的时候*/
    @Override
    protected void onStart() {
        mSurfaceHolder = mSurfaceview.getHolder();
        mSurfaceHolder.addCallback(this);  //SurfaceHolder加入回调接口
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置显示器类型(必要）

        String tempStr ;
        //上传实时视频所需操作:上传配置相关参数
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(sendRealtimeVideo_Activity.this);
        pUsername = preferences.getString("Username","BAITU");
        serverUrl = preferences.getString("ServerUrl","192.168.1.100");
        tempStr = preferences.getString("ServerPort","8888");
        serverPort = Integer.parseInt(tempStr);
        tempStr = preferences.getString("VideoPreRate", "1");
        VideoPreRate=Integer.parseInt(tempStr);
        tempStr = preferences.getString("VideoQuality", "85");
        VideoQuality = Integer.parseInt(tempStr);
        tempStr = preferences.getString("VideoWidthRatio", "100");
        VideoWidthRatio = Integer.parseInt(tempStr);
        tempStr = preferences.getString("VideoHeightRatio", "100");
        VideoHeightRatio=Integer.parseInt(tempStr);
        VideoWidthRatio=VideoWidthRatio/100f;
        VideoHeightRatio=VideoHeightRatio/100f;

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            if (mCamera != null) {
                mCamera.setPreviewCallback(null); // ！！这个必须在前，不然退出出错
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            Toast.makeText(sendRealtimeVideo_Activity.this,
                    "surfaceCreated调用",
                    Toast.LENGTH_SHORT).show();
            if (mCamera != null) {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            }
        }catch (Exception e){
            if(null != mCamera){
                mCamera.release();
                mCamera = null;
            }
            e.printStackTrace();
            Toast.makeText(sendRealtimeVideo_Activity.this,
                    "启动摄像头失败，请开启摄像头权限",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Toast.makeText(sendRealtimeVideo_Activity.this,
                "surfaceChanged调用",
                Toast.LENGTH_SHORT).show();
        if (mCamera == null) {
            return;
        }
        mCamera.stopPreview();
        mCamera.setPreviewCallback(this);
        mCamera.setDisplayOrientation(90); //设置横行录制
        //获取摄像头参数
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        VideoWidth=size.width;
        VideoHeight=size.height;
        VideoFormatIndex=parameters.getPreviewFormat();

        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(null != mCamera){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    /**初始化摄像头*/
    private void InitCamera(){
        //对Camera进行设置
        /*
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = parameters.getPreviewSize();

        parameters.setFlashMode("off");//无闪光灯
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setPreviewFormat(ImageFormat.YV12);
        parameters.setPictureSize(size.width,size.height);
        parameters.setPictureSize(size.width,size.height);
        mCamera.setParameters(parameters);

        //横竖屏幕自动调整
        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            parameters.set("orientation","portrait");
            parameters.set("rotation",90);
            mCamera.setDisplayOrientation(90);
        }else{
            parameters.set("orientation","landscape");
            mCamera.setDisplayOrientation(0);
        }
        byte[] buf = new byte[size.width * size.height * 3 / 2];
        mCamera.addCallbackBuffer(buf);
        mCamera.setPreviewCallback(this);

         */
        try{
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(sendRealtimeVideo_Activity.this,"初始化摄像头失败！",Toast.LENGTH_SHORT).show();
        }
    }

    /*
      onPreviewFrame:上传视频
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(!startSendVideo){
            return;  //若不通过则返回
        }
        if(tempPreRate < VideoPreRate){
            tempPreRate ++;
            return;
        }
        tempPreRate = 0;  //？？？这一步的作用？？？
        try{
            if(data != null){
                YuvImage image = new YuvImage(data, ImageFormat.NV21,VideoWidth,VideoHeight,null);
                if(image != null){
                    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                    //在此设置图片的尺寸和质量
                    image.compressToJpeg(new Rect(0, 0, (int)(VideoWidthRatio*VideoWidth),
                            (int)(VideoHeightRatio*VideoHeight)), VideoQuality, outstream);
                    //outstream.flush();
                    //启用线程将图像数据发送出去
                    Thread th = new MySendFileThread(outstream,pUsername,serverUrl,serverPort);
                    th.start();

                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /*
      MySendFileThread:发送文件线程
     */
    class MySendFileThread extends Thread{
        private String username;
        private String ipname;
        private int port;
        private byte byteBuffer[] = new byte[1024];
        private OutputStream outsocket;
        private ByteArrayOutputStream myoutputstream;

        public MySendFileThread(ByteArrayOutputStream myoutputstream,String username,String ipname,int port){
            this.myoutputstream = myoutputstream;
            this.username = username;
            this.ipname = ipname;
            this.port = port;
            try {
                myoutputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /*
           run：尝试进行视频传输的socket连接进程
         */
        public void run(){
            try {
                //将图像数据通过Socket发送出去
                Socket tempSocket = new Socket(ipname, port);
                outsocket = tempSocket.getOutputStream();//客户端输出流即是发送给服务器端的数据
                //写入头部数据信息:phonevideo|用户名|
                //String msg = java.net.URLEncoder.encode("PHONEVIDEO|"+username+"|","utf-8");
                String msg = java.net.URLEncoder.encode(socketCommand.uploadRealtimeVideo(username),"utf-8");
                byte[] buffer = msg.getBytes();
                outsocket.write(buffer);//将命令传给服务器端

                ByteArrayInputStream inputStream = new ByteArrayInputStream(myoutputstream.toByteArray());
                int amount;
                while((amount = inputStream.read(byteBuffer)) != -1){
                    outsocket.write(byteBuffer,0,amount);
                }
                myoutputstream.flush();
                myoutputstream.close();
                tempSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
