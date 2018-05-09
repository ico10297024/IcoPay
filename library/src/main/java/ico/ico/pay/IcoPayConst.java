package ico.ico.pay;

/**
 * Created by ICO on 2017/4/17 0017.
 */
public class IcoPayConst {

    /**
     * 微信支付回调是在一个activity中,所以通过该广播进行传递
     */
    public final static String ACTION_PAY_RESULT = "ico.ico.pay.result";

    /**
     * 支付结果
     * {@see #PR_CANCEL} 支付取消
     * {@see #PR_FAIL} 支付失败
     * {@see #PR_SUCCESS} 支付成功
     */
    public final static String EXTRA_PAY_RESULT = "payResult";


    /**
     * 支付平台
     * {@see #PP_WXPAY} 微信支付
     * {@see #PP_ALIPAY} 支付宝
     */
    public final static String EXTRA_PAY_PLATFORM = "payPlatform";
    /**
     * 官方的支付结果状态码
     */
    public final static String EXTRA_PAY_STATUS = "payStatus";
    /**
     * 官方的支付结果信息
     */
    public final static String EXTRA_PAY_MESSAGE = "payMessage";

    public final static int PR_CANCEL = 0;
    public final static int PR_FAIL = 1;
    public final static int PR_SUCCESS = 2;

    public final static int PP_WXPAY = 1;
    public final static int PP_ALIPAY = 2;


}