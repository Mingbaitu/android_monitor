package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.utils.Camera;
import com.hb.dialog.myDialog.ActionSheetDialog;
import com.hb.dialog.myDialog.MyAlertDialog;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout draw;
    private Button return_home;//用于试验侧滑页面
    private Button send;

    private Button recieve;
    private String MAC = "12345"; //试验获取MAC地址

    /*初始化摄像头类*/
    private Camera rec_camera;
    /*摄像头的名称*/
    private String[] camera_name = {"摄像头1","摄像头2","摄像头3","摄像头4","摄像头5","摄像头6","摄像头7","摄像头8","摄像头9","摄像头10","摄像头11","摄像头12"};
    /*摄像头图标*/
    private int[] camera_icon={R.mipmap.camera1,R.mipmap.camera2,R.mipmap.camera3,R.mipmap.camera1,R.mipmap.camera2,
            R.mipmap.camera3,R.mipmap.camera1,R.mipmap.camera2,R.mipmap.camera3,R.mipmap.camera1,R.mipmap.camera2,R.mipmap.camera3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        draw = (DrawerLayout)findViewById(R.id.draw);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.home_user);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw.openDrawer(Gravity.LEFT);
            }
        });

        rec_camera = new Camera();

        return_home = findViewById(R.id.btn2_test);
        //点击侧滑窗口的按钮收回侧滑窗口
        return_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw.closeDrawer(Gravity.LEFT);
            }
        });

        //跳转发送页面
        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent机制：是一种运行时绑定（run-time binding）机制，它能在程序运行过程中连接两个不同的组件
                //Intent i = new Intent(MainActivity.this,sendPhoto_Activity.class);
                //启动
                //startActivity(i);
                ActionSheetDialog dialog = new ActionSheetDialog(MainActivity.this)
                        .builder()
                        .setTitle("请选择")
                        .addSheetItem("发送视频", null, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int i) {
                                Intent intent = new Intent(MainActivity.this,sendVideo_Activity.class);
                                //启动
                                startActivity(intent);
                            }
                        }).addSheetItem("发送照片", null, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int i) {
                                //Intent机制：是一种运行时绑定（run-time binding）机制，它能在程序运行过程中连接两个不同的组件
                                Intent intent = new Intent(MainActivity.this,sendPhoto_Activity.class);
                                //启动
                                startActivity(intent);
                            }
                        });
                dialog.show();
            }
        });

        //跳转摄像头选择页面
        recieve = findViewById(R.id.receive);
        recieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this,cameraChoose_Activity.class);
                rec_camera.setIcon(camera_icon);
                rec_camera.setName(camera_name);
                // 接收服务器的数据信息
                ActionSheetDialog dialog = new ActionSheetDialog(MainActivity.this)
                        .builder()
                        .setTitle("请选择")
                        .addSheetItem("接收视频", null, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int i) {
                                intent.putExtra("页面","video");
                                intent.putExtra("rec_camera",rec_camera);
                                //启动
                                startActivity(intent);
                            }
                        }).addSheetItem("接收照片", null, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int i) {
                                intent.putExtra("页面","pic");
                                intent.putExtra("rec_camera",rec_camera);
                                //启动
                                startActivity(intent);
                            }
                        });
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_settings,menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //return super.onMenuOpened(featureId, menu);
        if(menu != null){
            if(menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")){
                try{
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu,true);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.server_setting:
                Intent intent = new Intent(MainActivity.this,settingActivity.class);
                startActivity(intent);
                break;
            case R.id.choose_authority:
                break;
            case R.id.device_info:
                //MAC = getLocalMacAddressFromIp();
                //MAC = getLocalMacAddressFromADBShell();
                MAC = getLocalMacAddressFromInterface();
                final MyAlertDialog myAlertDialog = new MyAlertDialog(MainActivity.this).builder()
                        .setTitle("手机设备信息")
                        .setMsg("MAC地址：" + MAC )
                        .setPositiveButton("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //showMsg("确认");
                            }
                        }).setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //showMsg("取消");
                            }
                        });
                myAlertDialog.show();
                break;
            default:
        }
        return true;
    }


    /*
        showMsg：各种类型的提示框显示提示框功能
     */
    private void showMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
      getLocalMacAddressFromIp:根据IP地址获取MAC地址
     */
    public static String getLocalMacAddressFromIp(){
        String strMacAddr = null;
        try{
            //获取IP地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer stringBuffer = new StringBuffer();
            for(int i = 0;i<b.length;i++){
                if(i != 0){
                    stringBuffer.append(":");
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                stringBuffer.append(str.length() == 1 ? 0+str:str);
            }
            strMacAddr = stringBuffer.toString().toUpperCase();
        }catch(Exception e){
            e.printStackTrace();
        }
        return strMacAddr;
    }

    /*
      getLocalMacAddressFromADBShell:使用adb shell命令来获取mac地址(无效）
    */
    public static String getLocalMacAddressFromADBShell(){
        String strMacAddr = null;
        String str = "";
        try{
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for(;null != str;){
                str = input.readLine();
                if(str != null){
                    strMacAddr = str.trim();
                    break;
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return strMacAddr;
    }

    /*
      getLocalMacAddressFromInterface:通过扫射网络接口来获取MAC地址
     */
    public static String getLocalMacAddressFromInterface(){
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }
    /*
       getLocalInetAddress:获取本机IP
     */
    public static InetAddress getLocalInetAddress(){
        InetAddress ip = null;
        try{
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while(en_netInterface.hasMoreElements()){
                NetworkInterface ni = (NetworkInterface)en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses(); //得到一个IP地址的列举
                while(en_ip.hasMoreElements()){
                    ip = en_ip.nextElement();
                    if(!ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")){
                        break;
                    }else{
                        ip = null;
                    }
                    if(ip != null){
                        break;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ip;
    }
}
