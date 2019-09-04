package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 导入数据
     * @param list
     */
    public void importList(List list);

    /**
     * 关键字搜索的方法
     */
    public Map<String,Object> searchItems(Map searchMap);

    /**
     * 删除数据
     * @param goodsIdList
     */
    public void deleteByGoodsIds(List goodsIdList);
}
