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
     * {@link #PR_CANCEL} 支付取消
     * {@link #PR_FAIL} 支付失败
     * {@link #PR_SUCCESS} 支付成功
     */
    public final static String EXTRA_PAY_RESULT = "payResult";


    /**
     * 支付平台
     * {@link #PP_WXPAY} 微信支付
     * {@link #PP_ALIPAY}
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

    /**
     * 支付取消
     */
    public final static int PR_CANCEL = 0;
    /**
     * 支付失败
     */
    public final static int PR_FAIL = 1;
    /**
     * 支付成功
     */
    public final static int PR_SUCCESS = 2;
    /**
     * 未知的支付结果
     * 支付宝说明:支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
     */
    public final static int PR_UNKNOWN = 3;

    /**
     * 微信支付
     */
    public final static int PP_WXPAY = 1;
    /**
     * 支付宝
     */
    public final static int PP_ALIPAY = 2;


}