# IcoPay
鉴于目前第三方支付分散的问题,哪怕是第三方支付整合的api服务都是需要money的,所以我自己对常用的几个第三方支付进行整合,并写成了api

支持:支付宝/微信/银联(银联有段时间没用了,目前版本不确定是否还能使用)

# 引入方式
目前还在审核中,可以先在资源目录中加入我的maven地址,就可以download到了
```
allprojects {
    repositories {
        maven { url "https://dl.bintray.com/ico10297024/maven/" }
        jcenter()
    }
}
```

## Gradle
```
compile 'ico.ico.pay:IcoPay:1.0.0'
```
## Maven
```
<dependency>
  <groupId>ico.ico.pay</groupId>
  <artifactId>IcoPay</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
## lvy
```
<dependency org='ico.ico.pay' name='IcoPay' rev='1.0.0'>
  <artifact name='IcoPay' ext='pom' ></artifact>
</dependency>
```


# 使用方式
## 1  在第三方支付平台获取应用唯一标识,比如appId等

## 2  配置你的AndroidManifest.xml
### 2.1 支付宝
```
<!--支付宝-->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<application>
  <!--<editor-fold desc="支付宝">-->
  <!--未安装支付宝时使用h5进行支付-->
  <activity
      android:name="com.alipay.sdk.app.H5PayActivity"
      android:configChanges="orientation|keyboardHidden|navigation|screenSize"
      android:exported="false"
      android:screenOrientation="behind"
      android:theme="@style/Theme.AppCompat.Light.NoActionBar"
      android:windowSoftInputMode="adjustResize|stateHidden"></activity>
  <!--支付宝h5账号验证-->
  <activity
      android:name="com.alipay.sdk.app.H5AuthActivity"
      android:configChanges="orientation|keyboardHidden|navigation"
      android:exported="false"
      android:screenOrientation="behind"
      android:theme="@style/Theme.AppCompat.Light.NoActionBar"
      android:windowSoftInputMode="adjustResize|stateHidden"></activity>
  <!--</editor-fold>-->
</application>

```
### 2.2 微信
```
<!--微信权限-->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<application>
  <!--<editor-fold desc="微信">-->
  <!--微信支付回调结果接收的界面，这个界面可以自定义，必须放在app包名下的.wxapi包下才会被调用-->
  <activity
      android:name="<package>.wxapi.WXPayEntryActivity"
      android:exported="true"
      android:launchMode="singleTop">
      <intent-filter>
          <action android:name="android.intent.action.VIEW" />
          <category android:name="android.intent.category.DEFAULT" />
          <data android:scheme="wx80c419c4f7a49583" />
      </intent-filter>
  </activity>
  <!--</editor-fold>-->
</application>
```
注:WXPayEntryActivity在library存在,开发者只要拷贝到自己的包里就可以,存放位置微信有严格规定,必须在<package>.wxapi.WXPayEntryActivity;

***注:由于WXPayEntryActivity是微信支付回调结果接收的界面,请务必拷贝使用library中的WXPayEntryActivity,否则可能导致微信支付无法通过广播接收支付结果***

***注:由于WXPayEntryActivity是微信支付回调结果接收的界面,请务必拷贝使用library中的WXPayEntryActivity,否则可能导致微信支付无法通过广播接收支付结果***

***注:由于WXPayEntryActivity是微信支付回调结果接收的界面,请务必拷贝使用library中的WXPayEntryActivity,否则可能导致微信支付无法通过广播接收支付结果***

### 2.3 银联
```
<!--<editor-fold desc="银联">-->
<activity
    android:name="com.unionpay.uppay.PayActivity"
    android:configChanges="orientation|keyboardHidden|keyboard"
    android:screenOrientation="portrait"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar"></activity>
<!--</editor-fold>-->
```

## 3  初始化支付环境
```
//支付宝1.0,前台进行签名,所以一些商户参数都写在前台
IcoPayUtil.initAlipayUtil(partner, seller, rsa_private, rsa_public);
//支付宝2.0
IcoPayUtil.initAlipay2Util();
//微信
IcoPayUtil.initWXUtil(context,appid);
//银联
IcoPayUtil.initUnionPayUtil();
```
## 4  获取对应支付平台的util
```
IcoPayUtil.getAlipayUtil();
IcoPayUtil.getAlipay2Util();
IcoPayUtil.getWxPayUtil();
IcoPayUtil.getUnionPayUtil();
```
## 5  进行支付
```
IcoPayUtil.getAlipayUtil().pay(Activity activity, String outTradeNo, String subject, String body, String price, String callbackUrl);

IcoPayUtil.getAlipay2Util().payV2(Activity activity, String appid, String rsa_private, String rsa2_private, String orderNo, String amount, String subject, String body);
IcoPayUtil.getAlipay2Util().payV2(Activity activity, String orderInfo);

IcoPayUtil.getWxPayUtil().pay(String appid, String noncestr, String partnerid, String prepayid, String timestamp, String sign);
IcoPayUtil.getUnionPayUtil().pay(Context context, String tn);
```
***注:支付宝2.0有两个支付函数,他们区别在于一种是在前端签名构造字符串,一种是服务器端签名构造字符串***
