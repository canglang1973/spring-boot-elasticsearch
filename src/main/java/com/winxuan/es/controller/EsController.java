package com.winxuan.es.controller;

import com.winxuan.es.model.AppCountDetail;
import com.winxuan.es.model.AppExceptionCountDetail;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms.Bucket;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author leitao.
 * @category
 * @time: 2018/9/25 0025-14:27
 * @version: 1.0
 * @description: 精确查询用term 组合查询用bool 范围用range    and查询用must    or查询用should  not查询用must not 
 * 常见的接收聚合返回结果的类型 ValueCount   AVG  SUM  MAX  MIN  按照英文意义就可以理解  分组聚合查询时候还需要根据实际情况看是返回那种terms 
 **/
@Controller
public class EsController {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private TransportClient transportClient;

    @RequestMapping("/get")
    public ResponseEntity get() throws IOException {
        GetRequest getRequest = new GetRequest("open_response-2018-09-26",
                "doc", "BVP0E2YBSJBm-RW3WfJc");
        //禁用_source检索，默认为启用
//        getRequest.fetchSourceContext(new FetchSourceContext(false));
        //同步执行
        GetResponse response = client.get(getRequest);
        return new ResponseEntity(response.getSource(), HttpStatus.OK);
    }

    @RequestMapping("/search")
    public ResponseEntity search() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchResponse response = client.search(searchRequest);
        return new ResponseEntity(response.getNumReducePhases(), HttpStatus.OK);
    }

    @RequestMapping("/get2")
    public ResponseEntity get2() throws IOException {
//        GetRequest getRequest = new GetRequest("shop_item_sync-2018-09-25",
//                "doc", "qbhSD2YBSOb0_w-CycCv");
        GetRequest getRequest = new GetRequest("open_response-2018-09-26",
                "doc", "BVP0E2YBSJBm-RW3WfJc");
        Map<String, Object> source = transportClient.get(getRequest).actionGet().getSource();
        return new ResponseEntity(source, HttpStatus.OK);
    }

    /**
     * 根据条件分页分页查询,默认页大小为10
     *
     * @return
     * @throws IOException
     */
    @RequestMapping("/search2")
    public ResponseEntity search2(int from, int size) throws IOException {
        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("body.appKey", "100071"));
//                .must(QueryBuilders.termQuery("created", "2018-09-26T03:35:19.000+0000"));
        SearchRequestBuilder sv = transportClient.prepareSearch("open_response-2018-09-26")
                .setTypes("doc").setQuery(qb).setFrom(from).setSize(size);
        SearchResponse response = sv.get();
        SearchHits searchHits = response.getHits();
        long totalHits = searchHits.getHits().length;
        for (SearchHit hit : searchHits.getHits()) {
            System.out.println(hit.getSourceAsString());
        }
        return new ResponseEntity(totalHits, HttpStatus.OK);
    }

    /**
     * 这段代码就相当于 sql select count(age) ageCount form accounts.person  where age >=30 and age<=30
     *
     * @return
     */
    @RequestMapping("/count")
    public ResponseEntity count() {
        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("created").from("2018-09-26T03:35:10.000+0000").to("2018-09-26T03:35:20.000+0000"));
        SearchRequestBuilder sv = transportClient.prepareSearch("open_response-2018-09-26").setTypes("doc")
                .setQuery(qb).setFrom(0).setSize(100);
        System.out.println(sv.toString());
        SearchResponse response = sv.get();
        SearchHits searchHits = response.getHits();
        for (SearchHit hit : searchHits.getHits()) {
            System.out.println(hit.getSourceAsString());
        }
        return new ResponseEntity(searchHits.getHits().length, HttpStatus.OK);
    }

    @RequestMapping("/count2")
    public ResponseEntity count2(String index, String appKey, boolean success) {
        AggregationBuilder termsBuilder = AggregationBuilders.count("count").field("created");
        QueryBuilder s = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("body.appKey", appKey))
                .must(QueryBuilders.termQuery("body.success", success));
        SearchRequestBuilder sv = transportClient.prepareSearch(index).setTypes("doc").setQuery(s)
                .setFrom(0).setSize(100).addAggregation(termsBuilder);
        System.out.println(sv.toString());
        SearchResponse response = sv.get();
        SearchHits searchHits = response.getHits();
        for (SearchHit hit : searchHits.getHits()) {
            System.out.println(hit.getSourceAsString());
        }
        ValueCount valueCount = response.getAggregations().get("count");
        return new ResponseEntity(valueCount.getValue(), HttpStatus.OK);
    }

    /**
     * 分组统计每个应用的调用次数,成功次数,失败次数
     *
     * @return
     */
    @RequestMapping("/group")
    public ResponseEntity group() {
        SearchRequestBuilder sv = transportClient.prepareSearch("open_response-2018-09-26").setTypes("doc")
                .setFrom(0).setSize(100);
        AggregationBuilder appKeyBuilder = AggregationBuilders.terms("appKeyCount").field("body.appKey.keyword")
                .size(100);
        AggregationBuilder successBuilder = AggregationBuilders.terms("successCount").field("body.success")
                .size(100);
        sv.addAggregation(appKeyBuilder.subAggregation(successBuilder));
        System.out.println(sv.toString());
        SearchResponse response = sv.get();
        Map<String, Aggregation> stringAggregationMap = response.getAggregations().asMap();
        StringTerms terms = (StringTerms) stringAggregationMap.get("appKeyCount");
        Iterator<Bucket> countBucketIt = terms.getBuckets().iterator();
        while (countBucketIt.hasNext()) {
            Bucket countBucket = countBucketIt.next();
            Map<String, Aggregation> successAggregationMap = countBucket.getAggregations().asMap();
            LongTerms successCountTerms = (LongTerms) successAggregationMap.get("successCount");
            Iterator<LongTerms.Bucket> successBucketIt = successCountTerms.getBuckets().iterator();
            long trueCount = 0L, falseCount = 0L;
            while (successBucketIt.hasNext()) {
                LongTerms.Bucket successBucket = successBucketIt.next();
                if (successBucket.getKeyAsString().equals("true")) {
                    trueCount = successBucket.getDocCount();
                }
                if (successBucket.getKeyAsString().equals("false")) {
                    falseCount = successBucket.getDocCount();
                }
            }
            System.out.println(countBucket.getKey() + "应用调用总次数:" + countBucket.getDocCount() + "次,成功:" + trueCount + "次,失败:" + falseCount + "次;");
        }
        return new ResponseEntity(null, HttpStatus.OK);
    }

    @RequestMapping("/gropbyapi")
    public ResponseEntity gropByAPI() {
        SearchRequestBuilder sv = transportClient.prepareSearch("open_response-2018-09-27").setTypes("doc");
        AggregationBuilder appKeyBuilder = AggregationBuilders.terms("appKeyCount").field("body.appKey.keyword")
                .size(10000);
        AggregationBuilder methodBuilder = AggregationBuilders.terms("methodCount").field("body.method.keyword")
                .size(10000);
        AggregationBuilder successBuilder = AggregationBuilders.terms("successCount").field("body.success")
                .size(2);
        sv.addAggregation(appKeyBuilder.subAggregation(methodBuilder.subAggregation(successBuilder)).subAggregation(successBuilder));
        SearchResponse response = sv.get();
        Map<String, Aggregation> stringAggregationMap = response.getAggregations().asMap();
        StringTerms terms = (StringTerms) stringAggregationMap.get("appKeyCount");
        Iterator<Bucket> countBucketIt = terms.getBuckets().iterator();
        List<AppCountDetail> appCountDetails = new ArrayList<AppCountDetail>();
        while (countBucketIt.hasNext()) {
            Bucket countBucket = countBucketIt.next();
            System.out.println("应用:" + countBucket.getKey() + "调用总次数:" + countBucket.getDocCount());
            Map<String, Aggregation> methodAggregation = countBucket.getAggregations().asMap();

            LongTerms successCountTerms = (LongTerms) methodAggregation.get("successCount");
            Iterator<LongTerms.Bucket> successBucketIt = successCountTerms.getBuckets().iterator();
            long trueTotalCount = 0L, falseTotalCount = 0L;
            while (successBucketIt.hasNext()) {
                LongTerms.Bucket successBucket = successBucketIt.next();
                if (successBucket.getKeyAsString().equals("true")) {
                    trueTotalCount = successBucket.getDocCount();
                }
                if (successBucket.getKeyAsString().equals("false")) {
                    falseTotalCount = successBucket.getDocCount();
                }
            }
            System.out.println("总成功:" + trueTotalCount + ",总失败:" + falseTotalCount);
            StringTerms methodTerms = (StringTerms) methodAggregation.get("methodCount");
            Iterator<Bucket> methodBucketlt = methodTerms.getBuckets().iterator();
            AppCountDetail detail = null;
            while (methodBucketlt.hasNext()) {
                Bucket methodBucket = methodBucketlt.next();
                detail = new AppCountDetail();
                detail.setAppKey(String.valueOf(countBucket.getKey()));
                detail.setTotal(countBucket.getDocCount());
                detail.setMethod(String.valueOf(methodBucket.getKey()));
                detail.setMethodTotal(methodBucket.getDocCount());
                detail.setSuccessed(trueTotalCount);
                detail.setFailed(falseTotalCount);

                Map<String, Aggregation> successAggregationMap = methodBucket.getAggregations().asMap();
                LongTerms methodSuccessCountTerms = (LongTerms) successAggregationMap.get("successCount");
                Iterator<LongTerms.Bucket> methodSuccessBucketIt = methodSuccessCountTerms.getBuckets().iterator();
                long trueCount = 0L, falseCount = 0L;
                while (methodSuccessBucketIt.hasNext()) {
                    LongTerms.Bucket methodSuccessBucket = methodSuccessBucketIt.next();
                    if (methodSuccessBucket.getKeyAsString().equals("true")) {
                        trueCount = methodSuccessBucket.getDocCount();
                        detail.setMethodSuccessed(trueCount);
                    }
                    if (methodSuccessBucket.getKeyAsString().equals("false")) {
                        falseCount = methodSuccessBucket.getDocCount();
                        detail.setMethodFailed(falseCount);
                    }
                }
                appCountDetails.add(detail);
                System.out.println("应用:" + countBucket.getKey() + "调用API:" + methodBucket.getKey() + ";总次数:" + methodBucket.getDocCount()
                        + ",成功:" + trueCount + ";失败:" + falseCount);
            }
        }
        return new ResponseEntity(appCountDetails, HttpStatus.OK);
    }

    /**
     * 按异常分组查询
     *
     * @return
     */
    @RequestMapping("/groupByExc")
    public ResponseEntity groupByExc() {
        SearchRequestBuilder sv = transportClient.prepareSearch("open_response-2018-09-27").setTypes("doc");
        AggregationBuilder appKeyBuilder = AggregationBuilders.terms("appKeyCount").field("body.appKey.keyword")
                .size(10000);
        AggregationBuilder methodBuilder = AggregationBuilders.terms("methodCount").field("body.method.keyword")
                .size(10000);
        AggregationBuilder exceptionTypeBuilder = AggregationBuilders.terms("exceptionTypeCount").field("body.exceptionType.keyword")
                .size(10000);
        sv.addAggregation(appKeyBuilder.subAggregation(methodBuilder.subAggregation(exceptionTypeBuilder)));
        SearchResponse response = sv.get();
        Map<String, Aggregation> asMap = response.getAggregations().asMap();
        StringTerms terms = (StringTerms) asMap.get("appKeyCount");
        Iterator<Bucket> appKeyCountlt = terms.getBuckets().iterator();
        List<AppExceptionCountDetail> details = new ArrayList<AppExceptionCountDetail>();
        while (appKeyCountlt.hasNext()) {
            Bucket appKeyBucket = appKeyCountlt.next();
            Map<String, Aggregation> methodAggregationMap = appKeyBucket.getAggregations().asMap();
            StringTerms methodTerms = (StringTerms) methodAggregationMap.get("methodCount");
            Iterator<Bucket> methodBucketlt = methodTerms.getBuckets().iterator();
            while (methodBucketlt.hasNext()) {
                Bucket methodBucket = methodBucketlt.next();
                Map<String, Aggregation> exceptionTypeMap = methodBucket.getAggregations().asMap();
                StringTerms exceptionTypeTerms = (StringTerms) exceptionTypeMap.get("exceptionTypeCount");
                Iterator<Bucket> exceptionTypeIterator = exceptionTypeTerms.getBuckets().iterator();
                AppExceptionCountDetail detail = null;
                while (exceptionTypeIterator.hasNext()) {
                    detail = new AppExceptionCountDetail();
                    Bucket exceptionBucket = exceptionTypeIterator.next();
                    System.out.println("应用:" + appKeyBucket.getKey()
                            + ",方法:" + methodBucket.getKey() + ",异常类型:" + exceptionBucket.getKey()
                            + ",次数:" + exceptionBucket.getDocCount());
                    detail.setAppKey(String.valueOf(appKeyBucket.getKey()));
                    detail.setMethod(String.valueOf(methodBucket.getKey()));
                    detail.setExceptionType(String.valueOf(exceptionBucket.getKey()));
                    detail.setTotal(exceptionBucket.getDocCount());
                    details.add(detail);
                }
            }
        }
        return new ResponseEntity(details, HttpStatus.OK);
    }

    /**
     * 所有调用量统计,失败+成功
     * @return
     */
    @RequestMapping("/appTotal")
    public ResponseEntity appTotal() {
        SearchRequestBuilder sv = transportClient.prepareSearch("open_response-2018-09-27").setTypes("doc");
        AggregationBuilder successBuilder = AggregationBuilders.terms("successCount").field("body.success")
                .size(2);
        sv.addAggregation(successBuilder);
        SearchResponse response = sv.get();
        Map<String, Aggregation> totalMap = response.getAggregations().asMap();
        LongTerms methodSuccessCountTerms = (LongTerms) totalMap.get("successCount");
        Iterator<LongTerms.Bucket> bucketIterator = methodSuccessCountTerms.getBuckets().iterator();
        long trueCount = 0L, falseCount = 0L;
        while (bucketIterator.hasNext()) {
            LongTerms.Bucket next = bucketIterator.next();
            if (next.getKeyAsString().equals("true")) {
                trueCount = next.getDocCount();
            }
            if (next.getKeyAsString().equals("false")) {
                falseCount = next.getDocCount();
            }
        }
        System.out.println("总次数:"+(trueCount + falseCount)+",成功:"+trueCount+",失败:"+falseCount);
        return new ResponseEntity(null, HttpStatus.OK);
    }

    /**
     * 所有异常次数统计
     * @return
     */
    @RequestMapping("/appException")
    public ResponseEntity appException() {
        SearchRequestBuilder sv = transportClient.prepareSearch("open_response-2018-09-27").setTypes("doc");
        AggregationBuilder exceptionTypeBuilder = AggregationBuilders.terms("exceptionTypeCount").field("body.exceptionType.keyword")
                .size(10000);
        sv.addAggregation(exceptionTypeBuilder);
        SearchResponse response = sv.get();
        Map<String, Aggregation> totalMap = response.getAggregations().asMap();
        StringTerms exceptionTypeTerms = (StringTerms) totalMap.get("exceptionTypeCount");
        Iterator<Bucket> exceptionTypeIterator = exceptionTypeTerms.getBuckets().iterator();
        while (exceptionTypeIterator.hasNext()) {
            Bucket exceptionBucket = exceptionTypeIterator.next();
            System.out.println("异常类型:" + exceptionBucket.getKey()+ ",次数:" + exceptionBucket.getDocCount()); }
        return new ResponseEntity(null, HttpStatus.OK);
    }

}
