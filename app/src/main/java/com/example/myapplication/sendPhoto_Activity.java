package com.example.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.utils.SocketCommand;
import com.example.myapplication.utils.UploadFile;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.example.myapplication.upload_img.bitmapHandle;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class sendPhoto_Activity  extends AppCompatActivity implements View.OnClickListener{
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_FROM_ALBUM = 2;
    private int set_choice; //设置选择摄像头or相册
    private SocketCommand socketCommand;    // 与服务器交互的命令类
    private Button take_photo;
    private Button upload_photo;
    private Button photo_connect_server;
    private Button add_notes;
    private Dialog mCameraDialog ;

    private ImageView picture;
    private Bitmap upload_img;
    private Uri imageUri;
    private String picPath = null;

    private String notes = null;

    private static final int FINISH = 0;

    /**用户名*/
    private String pUsername="BAITU";
    /**服务器地址*/
    private String serverUrl="192.168.1.8";
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_photo);

        socketCommand = new SocketCommand();

        picture = (ImageView)findViewById(R.id.preview_photo);

        //获取图片
        take_photo = findViewById(R.id.take_photo);
        take_photo.setOnClickListener(this);

        //添加注释
        add_notes = findViewById(R.id.add_notes);
        add_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(sendPhoto_Activity.this).builder()
                        .setTitle("请输入")
                        .setEditText("");
                myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMsg(myAlertInputDialog.getResult());
                        // String myAlertInputDialog.getResult() 即为获取到的文字信息
                        notes = myAlertInputDialog.getResult();
                        myAlertInputDialog.dismiss();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMsg("取消");
                        notes = " ";
                        myAlertInputDialog.dismiss();
                    }
                });
                myAlertInputDialog.show();
            }
        });

        //连接服务器
        photo_connect_server = findViewById(R.id.photo_connect_server);
        photo_connect_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String phoneconnect = "PHONECONNECT|"+"Baitu"+"|";
                Thread th = new MySendCommandThread(socketCommand.connectServer(pUsername,MAC));
                th.start();
            }
        });

        //上传图片
        upload_photo = findViewById(R.id.upload_photo);
        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*1.从ImageView控件获取Bitmap文件*/
                upload_img = ((BitmapDrawable)(picture.getDrawable())).getBitmap();
                VideoHeight = upload_img.getHeight();
                VideoWidth = upload_img.getWidth();
                if(upload_img != null) {
                    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                    //在此设置图片的尺寸和质量
                    Bitmap upload_img_temp = bitmapHandle.zooBitmap(upload_img,VideoWidth,VideoHeight);
                    try{
                        if(upload_img_temp.compress(Bitmap.CompressFormat.JPEG,100,outstream)){
                            outstream.flush();
                            outstream.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    //upload_img_temp.compress(Bitmap.CompressFormat.JPEG,100,outstream);
                    //启用线程将图像数据发送出去
                    Thread th2 = new MySendFileThread(outstream, pUsername, serverUrl, serverPort);
                    th2.start();

                    //提示框在收到服务器响应后提示图片上传成功或者失败

                }

            }
        });

        /*
        upload_photo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                /*
                上传图片思路：1.从ImageView控件获取Bitmap文件
                              2.将bitmap文件转换为字符流
                              3.将字符流上传到服务端
                              4.在服务端显示并存储图片文件
                 */
                //1.从ImageView控件获取Bitmap文件
         /*
                upload_img = ((BitmapDrawable)(picture.getDrawable())).getBitmap();
                if(upload_img != null){
                    //uploadImageFromHttp();
                    //uploadImageFromSocket();
                    //提示框显示上传图片
                    /*
                    MyImageMsgDialog myImageMsgDialog = new MyImageMsgDialog(sendPhoto_Activity.this).builder()
                            .setImageLogo(getResources().getDrawable(R.mipmap.ic_launcher)).setMsg("图片上传中...");
                    ImageView logoImg = myImageMsgDialog.getLogoImg();
                    logoImg.setImageResource(R.drawable.anim_connecting);
                    AnimationDrawable connectAnimation = (AnimationDrawable)logoImg.getDrawable();
                    connectAnimation.start();
                    myImageMsgDialog.show();

                     */
         /*
                }
            }
        });
        */

    }

    /*
        showMsg：各种类型的提示框显示提示框功能
     */
    private void showMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(sendPhoto_Activity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
      获取照片的触发按钮事件
     */
    public void onClick(View view){
        switch(view.getId()){
            case R.id.take_photo:
                setDialog();
                break;
            case R.id.btn_open_album:
                getPhoto_From_Album();
                break;
            case R.id.btn_open_camera:
                getPhoto_From_Camera();
                break;
            case R.id.btn_cancel:
                mCameraDialog.cancel();
                break;
            default:
                break;
        }
    }

    /*
       getPhoto_From_Album:从相册获取照片
     */
    private void getPhoto_From_Album(){
        //首先关闭底部滑出页面
        mCameraDialog.cancel();
        set_choice = 2;//表示选择相册
        //如果无权限即申请权限
        if(ContextCompat.checkSelfPermission(sendPhoto_Activity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //申请权限
            ActivityCompat.requestPermissions(sendPhoto_Activity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        //打开相册
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");//选择照片
        startActivityForResult(intent,CHOOSE_FROM_ALBUM);
    }
    /*
       getPhoto_From_Camera:从摄像头实时拍照并返回
     */
    private void getPhoto_From_Camera(){
        //首先关闭底部滑出页面
        mCameraDialog.cancel();
        set_choice = 1;//表示选择摄像头现场拍照
        //创建一个File文件对象用于存放摄像头拍下的照片
        File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
        picPath = getExternalCacheDir()+"/output_image.jpg";
        try{
            if(outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 24){
            //安卓版本高于7.0
            imageUri = FileProvider.getUriForFile(sendPhoto_Activity.this,
                    "com.example.cameraalbumtest.fileprovider",outputImage);
                    /*
                      参数1：context对象
                      参数2：任意唯一字符串
                      参数3：文件对象
                     */
        }else{
            imageUri = Uri.fromFile(outputImage);
        }
        //使用隐式Intent，系统会找到对应活动：调用摄像头并存储
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHOTO);

        //将拍摄照片显示出来
        try{
            String imagePath = getContentResolver().openInputStream(imageUri).toString();
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            picture.setImageBitmap(bitmap);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /*
       setDialog:设置选择从相册打开照片还是从摄像头直接拍照
     */
    private void setDialog(){
        mCameraDialog = new Dialog(this,R.style.BottomGetpath);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.send_photo_getpath,null);
        //初始化视图
        root.findViewById(R.id.btn_open_album).setOnClickListener(this);
        root.findViewById(R.id.btn_open_camera).setOnClickListener(this);
        root.findViewById(R.id.btn_cancel).setOnClickListener(this);

        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();//获取对话框当前参数值

        lp.x = 0;
        lp.y = 0;
        lp.width = (int)getResources().getDisplayMetrics().widthPixels;
        root.measure(0,0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f;
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //switch语句显示一定要正规
        switch (set_choice){
            case 1:
               try{
                   Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                   picture.setImageBitmap(bitmap);//将图像显示出来
               }catch(Exception e){
                   e.printStackTrace();
               }
               break;
            case 2:
                //判断手机版本
                if(Build.VERSION.SDK_INT >= 19){
                    handleImageOnKitKat(data);
                }else{
                    handelImageBeforeKitKat(data);
                }
                break;
            default:
                break;
        }
    }

    /*
        handleImageBeforeKitKat:对版本小于19的安卓系统读取相册照片并显示
     */
    private void handelImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }
    /*
        handleImageOnKitKat:针对版本大于19的安卓系统读取相册照片并显示
     */
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        //提示信息，可以不要
        Log.d("TAG","handelImageOnKitKat:uri  is "+uri.getScheme());
        Log.d("TAG","handelImageOnKitKat:uri is "+uri);

        if(DocumentsContract.isDocumentUri(this,uri)){
            //若为document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);

            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }else if("content".equalsIgnoreCase(uri.getScheme())){
                //如果是content类型的Uri，则用普通方式处理
                imagePath = getImagePath(uri,null);
                Log.d("tag","path:"+imagePath);
                //如果是google相册内的图片
                imagePath = getImageUrlWithAuthority(getApplicationContext(),uri);

            }else if("file".equalsIgnoreCase(uri.getScheme())){
                // 如果是file类型的Uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
            displayImage(imagePath); //显示图片
        }
    }

    /*
      getImagePath:获取图像的存储路径(api>19)
     */
    private String getImagePath(Uri uri,String selection){
        String path = null;
        //通过Uri与selection来从相册获取真实图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex( MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /*
       getImageUrlWithAuthority:从Google相册图片获取路径
     */
    public static String getImageUrlWithAuthority(Context context,Uri uri){
        InputStream is = null;
        if(uri.getAuthority()!=null){
            try{
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp).toString();

            }catch(Exception e){
                e.printStackTrace();
            }finally {
                try{
                    is.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /*
       writeToTempImageAndGetPathUri:将图片流读取出来保存到本地相册中
     */
    public static Uri writeToTempImageAndGetPathUri(Context inContext,Bitmap inImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver()
                ,inImage,"Title",null);
        return Uri.parse(path);
    }

    /*
      displayImage:显示图片
     */
    public void displayImage(String imagePath){
        if(imagePath != null){
            if(imagePath.endsWith("jpg") || imagePath.endsWith("png")){
                picPath = imagePath;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }


    /*
       uploadImageFromHttp:从HttpURLConnection方式上传图片
     */
    public void uploadImageFromHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 2.先压缩图片，再将bitmap文件转换为字符流文件/可以不压缩
                upload_img = bitmapHandle.zooBitmap(upload_img,256,256);
                byte[] bytes = bitmapHandle.Bitmap2Bytes(upload_img);
                if(picPath != null){
                    final File bitmapFile =  bitmapHandle.getFileFromBytes(bytes, picPath);
                    if(bitmapFile != null){
                        String request = bitmapHandle.uploadImage(bitmapFile,"http://up.imgapi.com/");
                    }
                }else{
                    Toast.makeText(sendPhoto_Activity.this,"请选择图片！",Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

    /*
      uploadImageFromSocket:以Socket方式上传图片
    */
    public void uploadImageFromSocket(){
        final Socket client;
        try {
            client = new Socket("192.168.1.7",8888);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                        UploadFile myFile = getUploadFile();
                        if(myFile != null){
                            oos.writeObject(myFile);
                            oos.close();
                        }else {

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            String result = bufferedReader.readLine();
            System.out.println("***********************"+result);
            if("true".equals(result)){
                Toast.makeText(sendPhoto_Activity.this,"操作成功！",Toast.LENGTH_SHORT);
            }else{
                Toast.makeText(sendPhoto_Activity.this,"操作失败！",Toast.LENGTH_SHORT);
            }
            bufferedReader.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    /*
       getUploadFile:获取上传文件
     */
    private UploadFile getUploadFile(){
        UploadFile myFile = new UploadFile();
        myFile.setTitle("白兔的摄影");
        myFile.setMimeType("image/jpeg");
        //File file = new File(picPath);

        //如何获取文件？？？
        // 2.先压缩图片，再将bitmap文件转换为字符流文件/可以不压缩
        upload_img = ((BitmapDrawable)(picture.getDrawable())).getBitmap();
        upload_img = bitmapHandle.zooBitmap(upload_img,256,256);
        byte[] bytes = bitmapHandle.Bitmap2Bytes(upload_img);  //获取到图片数组
        myFile.setContentData(bytes);
        myFile.setContentLength(bytes.length);
        myFile.setExt("jpg");
        /*
        if(picPath != null){
            final File bitmapFile =  bitmapHandle.getFileFromBytes(bytes, picPath);
            if(bitmapFile != null){
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(bitmapFile);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte data[] = new byte[1024];
                    int len = 0;
                    while((len = inputStream.read(data)) != -1){
                        bos.write(data,0,len);
                    }
                    myFile.setContentData(bos.toByteArray());
                    myFile.setContentLength(bitmapFile.length());
                    myFile.setExt("jpg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            Toast.makeText(sendPhoto_Activity.this,"请选择图片！",Toast.LENGTH_LONG).show();
        }

         */
        return myFile;
    }


    /*
       MySendCommandThread:发送命令线程，以连接服务器
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
                Socket socket = new Socket("192.168.1.8",8888);//设置服务器IP、端口号
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(command);
                out.flush(); //清空输出流
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    /*
       MySendFileThread:发送文件线程，以确保图片可以传输过去
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
                String msg = java.net.URLEncoder.encode(socketCommand.uploadPhoto(username,notes),"utf-8");
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


/*
   尝试过的失败代码
   1、关于上传图片按钮的响应类函数：SendOnClickListener
       --------错误：网络连接禁止在主线程中进行
       --------解决方案：新增类方法，在其中进行Socket的连接
   2、图像数据格式转换：将位图数据bitmap转化为Yuv数据
       --------错误：图像传输后出现花图
       --------解决方案：位图数据通过compress函数转为JPEG格式然后进行传输，无需转换为YUVimage数据
 */
