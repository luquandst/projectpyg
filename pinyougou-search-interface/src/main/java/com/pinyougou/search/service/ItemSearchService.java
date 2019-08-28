package com.pinyougou.search.service;

import java.util.Map;

public interface ItemSearchService {

    /**
     * 关键字搜索的方法
     */
    public Map<String,Object> searchItems(Map searchMap);

}
