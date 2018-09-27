package com.winxuan;

import com.winxuan.es.Application;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author leitao.
 * @category
 * @time: 2018/9/25 0025-14:14
 * @version: 1.0
 * @description:
 **/
@RunWith(SpringJUnit4ClassRunner.class)
public class EsTest {

    @Autowired
    private TransportClient client;

    @Test
    public void test(){
        GetResponse response = client.prepareGet("shop_item_compute-2018-08-27", "SHOP_ITEM_COMPUTE", "PJFtemUBnHxKlCMROalu").get();
        System.out.println(response);
    }


}
