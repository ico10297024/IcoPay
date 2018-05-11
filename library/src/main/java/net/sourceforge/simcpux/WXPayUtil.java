package net.sourceforge.simcpux;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Created by ICO on 2016/4/11 0011.
 * resp.errCode
 * -1失败
 * -2取消
 * 0成功
 */
public class WXPayUtil {

    public static final String PARAM_APPID = "appid";
    public static final String PARAM_NONCESTR = "noncestr";
    public static final String PARAM_PARTNERID = "partnerid";
    public static final String PARAM_PREPAYID = "prepayid";
    public static final String PARAM_TIMESTAMP = "timestamp";
    public IWXAPI msgApi;

    /**
     * 初始化微信支付环境
     *
     * @param context 当前上下文,推荐使用{@link android.app.Application}
     * @param appId   应用的appId
     */
    public WXPayUtil(Context context, String appId) {
        msgApi = WXAPIFactory.createWXAPI(context, appId, false);
        // 将该app注册到微信
        boolean reg = msgApi.registerApp(appId);
        Log.w("ico_srpay", "WXPayUtil: " + reg);
    }


    /**
     * 检查当前安装的微信版本是否支持支付
     *
     * @return boolean
     */
    public boolean isPaySupported() {
        return msgApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
    }

    /**
     * 检查当前系统是否安装了微信
     *
     * @return boolean
     */
    public boolean isWXAppInstalled() {
        return msgApi.isWXAppInstalled();
    }

    /**
     * 发起微信支付
     *
     * @param appid     应用的appId
     * @param noncestr  随机字符串,不长于32位
     * @param partnerid 微信支付分配的商户号
     * @param prepayid  微信返回的预支付交易回话ID
     * @param timestamp 时间戳
     * @param sign      签名,可以通过{@link #genAppSign(String, String, String, String, String, String)} 进行签名
     * @return
     */
    public boolean pay(String appid, String noncestr, String partnerid, String prepayid, String timestamp, String sign) {
        PayReq req = new PayReq();
        req.appId = appid;
        req.nonceStr = noncestr;
        req.packageValue = "Sign=WXPay";
        req.partnerId = partnerid;
        req.prepayId = prepayid;
        req.timeStamp = timestamp;
        req.sign = sign;
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        return msgApi.sendReq(req);
    }

    /**
     * 生成app的sign，用于支付参数
     *
     * @param appid     应用的appId
     * @param noncestr  随机字符串,不长于32位
     * @param partnerid 微信支付分配的商户号
     * @param prepayid  微信返回的预支付交易回话ID
     * @param timestamp 时间戳
     * @param apiKey    商户平台设置的秘钥
     * @return String
     */
    private String genAppSign(String appid, String noncestr, String partnerid, String prepayid, String timestamp, String apiKey) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put(PARAM_APPID, appid);
        parameters.put(PARAM_NONCESTR, noncestr);
        parameters.put(PARAM_PARTNERID, partnerid);
        parameters.put(PARAM_PREPAYID, prepayid);
        parameters.put(PARAM_TIMESTAMP, timestamp);
        parameters.put("package", "Sign=WXPay");
        return createSign("UTF-8", parameters, apiKey);
    }

    /**
     * 微信支付签名算法sign
     *
     * @param characterEncoding 编码方式,一般使用"UTF-8"
     * @param parameters 参数集
     * @return
     */
    @SuppressWarnings("unchecked")
    private String createSign(String characterEncoding, SortedMap<Object, Object> parameters, String apiKey) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + apiKey);
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }


    /**
     * 需要在微信的结果页面{@link ico.ico.pay.wxapi.WXPayEntryActivity}中调用来处理
     * onCreate、onNewIntent
     *
     * @param intent 在结果页面{@link ico.ico.pay.wxapi.WXPayEntryActivity} 可以通过{@link Activity#getIntent()},或者通过函数给定的参数获取
     * @param handler 用来接收微信的回调
     * @return
     */
    public boolean handleResponseIntent(Intent intent, IWXAPIEventHandler handler) {
        return msgApi.handleIntent(intent, handler);
    }
}
