package com.example.myapplication.utils;

/*
   socketCommand:命令类（与服务器进行交互）
 */
public class SocketCommand {
    String command;
    public SocketCommand(){
        command = "";
    }
    /*
      disconnectServer:断开服务器连接
     */
    public String disconnectServer(String pUsername){
        command = "PHONEDISCONNECT|"+pUsername+"|";
        return  command;
    }
    /*
      connectServer:连接服务器(仅有用户名）
     */
    public String connectServer(String pUsername){
        command = "PHONECONNECT|"+pUsername+"|";
        return  command;
    }
    public String connectServer(String pUsername,String MAC){
        command = "PHONECONNECT|"+pUsername+"|"+ MAC + "|";
        return  command;
    }
    /*
       uploadRealtimeVideo:上传实时视频
     */
    public String uploadRealtimeVideo(String pUsername){
        command = "PHONEVIDEO|"+pUsername+"|";
        return  command;
    }
    public String uploadRealtimeVideo(String pUsername,String notes){
        command = "PHONEVIDEO|"+pUsername+"|" + notes + "|";
        return  command;
    }
    /*
       uploadPhoto:上传图像
     */
    public String uploadPhoto(String pUsername,String notes){
        command = "PHONEPHOTO|"+pUsername+"|" + notes + "|";
        return command;
    }
}
