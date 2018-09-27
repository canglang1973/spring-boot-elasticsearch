package com.winxuan.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author leitao.
 * @category
 * @time: 2018/9/25 0025-14:09
 * @version: 1.0
 * @description:
 **/
@Configuration
public class EsConfig {

    @Bean
    public RestHighLevelClient client() throws UnknownHostException {
        //生产的client
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("10.1.5.168", 9200, "http")));
        return client;
    }

//    @Bean
//    public TransportClient transportClient() throws UnknownHostException {
//        TransportAddress node = new TransportAddress(
//                InetAddress.getByName("10.100.12.123"),9300
//        );
//        Settings settings = Settings.builder()
//                .put("cluster.name","wx-log-cluster")
//                .build();
//        TransportClient client = new PreBuiltTransportClient(settings);
//        client.addTransportAddress(node);
//        return client;
//    }
    @Bean
    public TransportClient transportClient() throws UnknownHostException {
        TransportAddress node = new TransportAddress(
                InetAddress.getByName("10.1.5.168"),9300
        );
        Settings settings = Settings.builder()
                .put("cluster.name","elasticsearch-cluster")
                .build();
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(node);
        return client;
    }

}
