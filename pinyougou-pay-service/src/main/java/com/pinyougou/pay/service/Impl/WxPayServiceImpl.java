package com.pinyougou.pay.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinoyougou.util.HttpClient;
import com.pinyougou.pay.service.WxpayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class WxPayServiceImpl implements WxpayService {

    /**
     * 获取属性的值
     */
    @Value("${appid}")
    private String appid;  //公众号id
    @Value("${partner}")
    private String partner; //商家id
    @Value("${partnerkey}")
    private String partnerkey; //商家密钥
    //发送请求的地址
    private String payUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";


    /**
     * 根据订单号和总费用生成支付的二维码
     * @param out_trade_no   //订单号
     * @param total_fee       //总的费用
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        System.out.println("out_trade_no = " + out_trade_no);
        System.out.println("total_fee = " + total_fee);
        //配置请求需要发送的所有参数，放置在一个map集合中
        Map<String,String> params = new HashMap<>();
        params.put("appid", appid);       //公众号id
        params.put("mch_id",partner);    //商户号
        params.put("nonce_str",WXPayUtil.generateNonceStr());  //随机字符串
        params.put("body","品优购");   //商品描述
        params.put("out_trade_no",out_trade_no);  //商户订单号
        params.put("total_fee",total_fee); //商品的总金额
        params.put("out_trade_no","www.baidu.com"); //通知地址
        params.put("trade_type","NARTIVE");    //交易的类型
        try {
            //将map集合转化为带有签名的xml文件
            String signedXml = WXPayUtil.generateSignedXml(params, partnerkey);

            //定义一个httpclient用来发送请求
            HttpClient httpClient = new HttpClient(payUrl);
            httpClient.setHttps(true);
            //将httpclient与xml进行绑定
            httpClient.setXmlParam(signedXml);
            //发送请求
            httpClient.post();

            //获取返回的结果
            String content = httpClient.getContent();
            //将结果转化为map集合
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            //定义一个map集合作为返回到controller层的结果
            Map<String,String> resultMap = new HashMap<>();
            //结果集合中添加值
            resultMap.put("code_url", map.get("code_url"));//支付地址
            resultMap.put("total_fee", total_fee);//总金额
            resultMap.put("out_trade_no",out_trade_no);//订单号
            //返回结果
            return resultMap;
        } catch (Exception e) {
            System.out.println("发生了异常...");
            e.printStackTrace();
            System.out.println("未发生异常...");
            return new HashMap();
        }

    }
}
