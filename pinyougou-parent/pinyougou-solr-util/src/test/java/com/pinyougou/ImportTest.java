package com.pinyougou;


import com.pinyougou.solr.utils.SolrUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext*.xml")
public class ImportTest {
    @Autowired
    private SolrUtils solrUtils;
    @Test
    public void execute() {
        solrUtils.importData();
    }
}
