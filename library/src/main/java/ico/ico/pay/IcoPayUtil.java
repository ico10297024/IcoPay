package ico.ico.pay;

import android.content.Context;

import com.alipay.sdk.pay.AlipayUtil;
import com.alipay.sdk.pay2.Alipay2Util;
import com.unionpay.uppayplugin.UnionPayUtil;

import net.sourceforge.simcpux.MD5Util;
import net.sourceforge.simcpux.WXPayUtil;

import java.util.HashMap;

/**
 * Created by ICO on 2017/4/6 0006.
 * <p>
 * 目前整合了支付宝.微信.银联 三种支付方式
 * <p>
 * 由于不同支付方式回调方式不同,为了统一一律采用广播形式
 * <p>
 * 目前广播已支持了支付宝1.0 2.0和微信
 * <p>
 * 所有get包含初始化功能,并且相同的传入参数将复用上次初始化的util
 */
public class IcoPayUtil {
    private static HashMap<String, AlipayUtil> alipayUtils = new HashMap<>();
    private static Alipay2Util alipay2Util;
    private static HashMap<String, WXPayUtil> wxPayUtils = new HashMap<>();
    private static UnionPayUtil unionPayUtil;

    /**
     * 初始化支付宝1.0sdk环境并获取支付宝1.0util
     *
     * @param partner     商户PID
     * @param seller      商户收款账号
     * @param rsa_private 商户私钥，pkcs8格式
     * @param rsa_public  支付宝公钥FF
     * @return {@link AlipayUtil}
     */
    public static AlipayUtil getAlipayUtil(String partner, String seller, String rsa_private, String rsa_public) {
        String s = partner + seller + rsa_private + rsa_public;
        String key = MD5Util.MD5Encode(s, "UTF-8");
        AlipayUtil alipayUtil = alipayUtils.get(key);
        if (alipayUtil == null) {
            alipayUtil = new AlipayUtil(partner, seller, rsa_private, rsa_public);
        }
        return alipayUtil;
    }

    /**
     * 初始化支付宝2.0sdk环境并获取支付宝2.0平台util
     *
     * @return {@link Alipay2Util}
     */
    public static Alipay2Util getAlipay2Util() {
        if (alipay2Util == null) {
            alipay2Util = new Alipay2Util();
        }
        return alipay2Util;
    }

    /**
     * 初始化微信sdk环境并获取微信平台util
     *
     * @return {@link WXPayUtil}
     */
    public static WXPayUtil getWxPayUtil(Context context, String appId) {
        WXPayUtil wxPayUtil = wxPayUtils.get(appId);
        if (wxPayUtil == null) wxPayUtil = new WXPayUtil(context, appId);
        return wxPayUtil;
    }

    /**
     * 初始化银联sdk环境并获取银联平台util
     *
     * @return {@link UnionPayUtil}
     */
    public static UnionPayUtil getUnionPayUtil() {
        if (unionPayUtil == null) {
            unionPayUtil = new UnionPayUtil();
        }
        return unionPayUtil;
    }
}
