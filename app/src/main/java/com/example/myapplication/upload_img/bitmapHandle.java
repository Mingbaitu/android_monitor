package com.example.myapplication.upload_img;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class bitmapHandle {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000;//超时时间
    private static final String CHARST = "utf-8"; //设置编码

    /*
      zooBitmap:图片缩放
     */
    public static Bitmap zooBitmap(Bitmap bitmap,int w,int h){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float)w/width);
        float scaleHeight = ((float)h/height);
        matrix.postScale(scaleWidth,scaleHeight);
        //Bitmap newbmp = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        return Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
    }
    /*
      Bitmap2Bytes:将bitmap转成byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG,100,baos);
        return baos.toByteArray();
    }
    /*
     getFileFromBytes:将字节数组保存为一个文件
     */
    public static File getFileFromBytes(byte[] b,String outputFile){
        BufferedOutputStream stream = null;
        File file = null;
        try{
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }
    /*
     uploadImage:android上传图像文件到服务器
     */
    public static String uploadImage(File file, String RequestURL){
        String result_end = "error";
        String BOUNDARY = UUID.randomUUID().toString(); //边界标识，随机生成
        String PREFIX = "--",LINE_END = "\r\n";
        String CONTENR_TYPE = "multipart/from-data"; //内容类型
        try{
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存
            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset",CHARST); //设置编码
            conn.setRequestProperty("connection","keep-alive");
            conn.setRequestProperty("Content-Type",CONTENR_TYPE+";boundary="+BOUNDARY);

            if(file != null){
                //将文件包装并上传
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(PREFIX + BOUNDARY + LINE_END);
                dos.writeBytes("Content-Disposition:from-data;" + "name=\"inputName\";filename=\"" + file.getName() + "\"" + LINE_END);
                dos.writeBytes(LINE_END);

                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = -1;
                while((len = fis.read(bytes)) != -1){
                    dos.write(bytes,0,len);
                }
                fis.close();
                dos.write(LINE_END.getBytes());

                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();

                /*
                获取响应码 200=成功，当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                if(res == 200){
                    InputStream input = conn.getInputStream();
                    StringBuilder sbs = new StringBuilder();
                    int ss;
                    while((ss = input.read()) != -1){
                        sbs.append((char)ss);
                    }
                    result_end = sbs.toString();
                    Log.i(TAG,"result--------------------->>" + result_end);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result_end;
    }
}
