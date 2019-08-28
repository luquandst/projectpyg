package com.pinyougou.test;

import com.pinyougou.util.SolrUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
public class TestSolr {

    @Autowired
    private SolrUtil solrUtil;

    @Test
    public void testImport(){
        //导入到索引库
        solrUtil.importItemsToSolr();
        System.out.println("导入到索引库成功....");
    }
}
