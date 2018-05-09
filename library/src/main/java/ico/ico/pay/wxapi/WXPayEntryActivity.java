package ico.ico.pay.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ico.ico.pay.IcoPayConst;
import ico.ico.pay.IcoPayUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * 将此类拷贝到app包
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);


        //这里的APP_ID传入正确的值
//        SRWXPayUtil = new SRWXPayUtil(this,APP_ID);
        IcoPayUtil.getWxPayUtil().handleResponseIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        IcoPayUtil.getWxPayUtil().handleResponseIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Intent intentBroadcast = new Intent(IcoPayConst.ACTION_PAY_RESULT);
        intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_PLATFORM, IcoPayConst.PP_WXPAY);
        switch (resp.errCode) {
            case -1://支付失败
                intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_RESULT, IcoPayConst.PR_FAIL);
                break;
            case -2://取消支付
                intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_RESULT, IcoPayConst.PR_CANCEL);
                break;
            case 0://支付成功
                intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_RESULT, IcoPayConst.PR_SUCCESS);
                break;
        }
        intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_MESSAGE, resp.errStr);
        intentBroadcast.putExtra(IcoPayConst.EXTRA_PAY_STATUS, resp.errCode);
        sendBroadcast(intentBroadcast);
        finish();
    }
}