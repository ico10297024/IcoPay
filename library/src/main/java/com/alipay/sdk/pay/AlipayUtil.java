package com.alipay.sdk.pay;

import android.app.Activity;
import android.content.Intent;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ico.ico.pay.IcoPayConst;

public class AlipayUtil {


    public static final int SDK_PAY_FLAG = 1;
    // 商户PID
    private String partner;
    // 商户收款账号
    private String seller;
    // 商户私钥，pkcs8格式
    private String rsa_private;
    // 支付宝公钥
    private String rsa_public;

    /**
     * 初始化支付宝支付环境
     *
     * @param partner     商户PID
     * @param seller      商户收款账号
     * @param rsa_private 商户私钥，pkcs8格式
     * @param rsa_public  支付宝公钥
     */
    public AlipayUtil(String partner, String seller, String rsa_private, String rsa_public) {
        this.partner = partner;
        this.seller = seller;
        this.rsa_private = rsa_private;
        this.rsa_public = rsa_public;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion(Activity activity) {
        PayTask payTask = new PayTask(activity);
        String version = payTask.getVersion();
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     * @param activity 当前活动页上下文
     * @param outTradeNo  订单号
     * @param subject     商品名称
     * @param body        商品详情
     * @param price       价格
     * @param callbackUrl 回调url
     */
    public void pay(final Activity activity, String outTradeNo, String subject, String body, String price, String callbackUrl) {
//		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
//			new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
//					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialoginterface, int i) {
//							//
//							finish();
//						}
//					}).show();
//			return;
//		}
        String orderInfo = getOrderInfo(outTradeNo, subject, body, price, callbackUrl);

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                handlePayResult(activity, SDK_PAY_FLAG, new PayResult(result));
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 处理支付结果,发送广播
     */
    private void handlePayResult(Activity activity, int flag, PayResult payResult) {
        switch (flag) {
            case SDK_PAY_FLAG: {
                /**
                 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                 */
                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                int resultStatus = Integer.valueOf(payResult.getResultStatus());

                Intent intentBroadcast = new Intent(IcoPayConst.ACTION_PAY_RESULT);
                intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_PLATFORM, IcoPayConst.PP_ALIPAY);
                switch (resultStatus) {
                    case 9000:
                        intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_RESULT, IcoPayConst.PR_SUCCESS);
                        break;
                    case 6001:
                        intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_RESULT, IcoPayConst.PR_CANCEL);
                        break;
                    case 8000:
                        intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_RESULT, IcoPayConst.PR_UNKNOWN);
                        break;
                    default:
                        intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_RESULT, IcoPayConst.PR_FAIL);
                        break;
                }
                intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_MESSAGE, resultInfo);
                intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_STATUS, resultStatus);
                activity.sendBroadcast(intentBroadcast);
                break;
            }
//                case SDK_AUTH_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
//                    String resultStatus = authResult.getResultStatus();
//
//                    // 判断resultStatus 为“9000”且result_code
//                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
//                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
//                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
//                        // 传入，则支付账户为该授权账户
//                        Toast.makeText(Alipay2Util.this,
//                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
//                                .show();
//                    } else {
//                        // 其它状态值则为授权失败
//                        Toast.makeText(Alipay2Util.this,
//                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
//
//                    }
//                    break;
//                }
            default:
                break;
        }
    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String outTradeNo, String subject, String body, String price, String callbackUrl) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + partner + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + seller + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + outTradeNo + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
//        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";
        orderInfo += "&notify_url=" + "\"" + callbackUrl + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, rsa_private);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
