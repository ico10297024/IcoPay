package com.alipay.sdk.pay2;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.pay2.util.OrderInfoUtil2_0;

import java.util.Map;

import ico.ico.pay.IcoPayConst;

/**
 * 重要说明:
 * <p>
 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
 */
public class Alipay2Util {
    static final int SDK_PAY_FLAG = 1;
    static final int SDK_AUTH_FLAG = 2;

    /**
     * 初始化支付宝支付2.0sdk环境
     */
    public Alipay2Util() {
    }

    /**
     * 支付宝支付业务
     * 需要传入私钥，本地加签
     * 商户私钥，pkcs8格式
     * 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个
     * 如果商户两个都设置了，优先使用 RSA2_PRIVATE
     * RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE
     * 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成，
     * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public void payV2(final Activity activity, String appid, String rsa_private, String rsa2_private, String orderNo, String amount, String subject, String body) {
        if (TextUtils.isEmpty(appid) || (TextUtils.isEmpty(rsa2_private) && TextUtils.isEmpty(rsa_private))) {
            throw new IllegalArgumentException("需要配置APPID | (RSA_PRIVATE && RSA2_PRIVATE)");
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (rsa2_private != null && rsa2_private.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(appid, rsa2, orderNo, amount, subject, body);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? rsa2_private : rsa_private;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                handlePayResult(activity, SDK_PAY_FLAG, new PayResult(result));
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 支付宝支付业务
     * 服务器加签
     */
    public void payV2(final Activity activity, final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                handlePayResult(activity, SDK_PAY_FLAG, new PayResult(result));
            }
        };

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
     * get the sdk version. 获取SDK版本号
     */
    public String getSDKVersion(Activity activity) {
        PayTask payTask = new PayTask(activity);
        String version = payTask.getVersion();
        return version;
    }

    public void enableSandbox() {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
    }
}
