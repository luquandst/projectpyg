package com.pinyougou.pay.service;

import java.util.Map;

public interface WxpayService {

    /**
     * 生成微信支付二维码
     * @param out_trade_no   //订单号
     * @param total_fee       //总的费用
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);
}
