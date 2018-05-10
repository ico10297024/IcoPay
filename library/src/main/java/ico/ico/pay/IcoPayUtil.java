package ico.ico.pay;

import android.content.Context;

import com.alipay.sdk.pay.AlipayUtil;
import com.alipay.sdk.pay2.Alipay2Util;
import com.unionpay.uppayplugin.UnionPayUtil;

import net.sourceforge.simcpux.WXPayUtil;

/**
 * Created by ICO on 2017/4/6 0006.
 * 目前整合了支付宝.微信.银联 三种支付方式
 * 由于不同支付方式回调方式不同,为了统一一律采用广播形式
 * 目前广播已支持了支付宝2.0和微信
 */
public class IcoPayUtil {
    private static AlipayUtil alipayUtil;
    private static Alipay2Util alipay2Util;
    private static WXPayUtil wxPayUtil;
    private static UnionPayUtil unionPayUtil;

    /**
     * 初始化支付宝sdk环境
     *
     * @param partner     商户PID
     * @param seller      商户收款账号
     * @param rsa_private 商户私钥，pkcs8格式
     * @param rsa_public  支付宝公钥
     */
    public static void initAlipayUtil(String partner, String seller, String rsa_private, String rsa_public) {
        if (alipayUtil == null) {
            alipayUtil = new AlipayUtil(partner, seller, rsa_private, rsa_public);
        }
    }

    /**
     * 初始化支付宝2.0sdk环境
     *
     * @param context 当前上下文,推荐使用{@link android.app.Application}
     */
    public static void initAlipay2Util(Context context) {
        if (alipay2Util == null) {
            alipay2Util = new Alipay2Util(context);
        }
    }

    /**
     * 初始化微信sdk环境
     *
     * @param context 当前上下文,推荐使用{@link android.app.Application}
     * @param appId   微信的appId
     */
    public static void initWXUtil(Context context, String appId) {
        if (wxPayUtil == null) {
            wxPayUtil = new WXPayUtil(context, appId);
        }
    }

    /**
     * 初始化银联sdk环境
     */
    public static void initUnionPayUtil() {
        if (unionPayUtil == null) {
            unionPayUtil = new UnionPayUtil();
        }
    }

    /**
     * 支付宝平台util
     *
     * @return {@link AlipayUtil}
     */
    public static AlipayUtil getAlipayUtil() {
        return alipayUtil;
    }

    /**
     * 支付宝2.0平台util
     *
     * @return {@link Alipay2Util}
     */
    public static Alipay2Util getAlipay2Util() {
        return alipay2Util;
    }

    /**
     * 微信平台util
     *
     * @return {@link WXPayUtil}
     */
    public static WXPayUtil getWxPayUtil() {
        return wxPayUtil;
    }

    /**
     * 银联平台util
     *
     * @return {@link UnionPayUtil}
     */
    public static UnionPayUtil getUnionPayUtil() {
        return unionPayUtil;
    }
}
