## Android_SDK

### Usage

#### 调用步骤

1、程序启动时调用 KhinfSDK.init(Context context) - 初始化

2、KhinfSDK.setUrl(String ip, String port, String serverName,boolean isHttps) - 设置服务器信息（不设置使用默认）

3、KhinfSDK.getInstance().login(Context context，String username, String password) - sdk进入主界面

#### 其他

KhinfSDK.getInstance().getNavFrament(Property property) - 获取导航fragment，需传入数据
KhinfSDK.getInstance().getNavFramentClass(Property property) - 获取导航fragment的class对象，需传入数据

KhinfSDK.getInstance().getCustomMainFrament(Property property) - 获取自定义首页fragment，需传入数据
KhinfSDK.getInstance().getCustomMainFramentClass(Property property) - 获取自定义首页fragment的class对象，需传入数据


