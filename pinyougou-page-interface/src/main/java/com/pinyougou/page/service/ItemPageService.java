package com.pinyougou.page.service;

public interface ItemPageService {

    /**
     * 根据商品的id生成静态页面
     * @param goodsId
     * @return
     */
    public boolean genItemHtml(Long goodsId);
}
