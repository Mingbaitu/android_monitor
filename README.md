#  android_monitor : An android application for video images monitoring and management
基于Socket通信传输原理，实现图像与注释的上传，实时视频的传输；【android studio版本】

## 功能介绍
- 支持手机安卓端连接服务器

- 支持MAC地址、手机号等本机信息的上传

- 支持图像以及文本等数据的上传

- 支持实时视频的上传

- 未完待续...

------

## 关于项目
>这是一个在android studio下开源项目，如需使用eclipse，需要进行项目格式的转换，在编译成功后可直接使用;

### 添加依赖

1.先在项目根目录的build.gradle的repositories添加：

```
allprojects {
     repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
2.然后在dependencies添加：

```
dependencies {
  ...
  //androidx项目
  implementation 'androidx.appcompat:appcompat:1.1.0'
  implementation 'androidx.preference:preference:1.1.0'
  implementation 'com.afollestad:drag-select-recyclerview:2.4.0'
  implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
  implementation 'com.github.darrenfantasy:DoubleDatePicker:1.0'
  testImplementation 'junit:junit:4.12'
  implementation 'androidx.core:core:1.2.0-beta01'
  implementation 'androidx.fragment:fragment:1.2.0-rc01'
  androidTestImplementation 'androidx.test:runner:1.2.0'
  androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
}
```

### 发送命令与数据包格式要求

| Command         | 数据包格式                       | 备注           |
| --------------- | -------------------------------- | -------------- |
| PHONECONNECT    | PHONECONNECT\|pUsername\|        | 连接服务器     |
| PHONEPHOTO      | PHONEPHOTO\|pUsername\|notes\|   | 上传图像       |
| PHONEVIDEO      | PHONEVIDEO\|pUsername\| notes \| | 实时视频连接   |
| PHONEDISCONNECT | PHONEDISCONNECT\|pUsername\|     | 断开服务器连接 |


### 技术点介绍
- 获取MAC地址（针对不同版本的android有不同获取方法）

- 调用摄像头以及相册

- 基于Socket设置ip地址与端口进行服务器连接

- 摄像头数据转换为输入流进行传输

- 图像bitmap文件转为输入流进行传输

- 未完待续...

### 界面预览

| 主页面                                                       | 发送图像页面                                                 | 实时视频传输                                                 |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![home_page.PNG](https://i.loli.net/2020/02/18/KV7WiLEIrDmtXZj.png) | ![send_photo.PNG](https://i.loli.net/2020/02/18/izxYqw5GghV7jdD.png) | ![send_video.PNG](https://i.loli.net/2020/02/18/Nap69wtVPKIG5CS.png) |


------

## Reference
[1]. https://github.com/getActivity/TitleBar

[2]. https://github.com/HanHuoBin/BaseDialog

[3]. https://github.com/darrenfantasy/DoubleDatePicker
