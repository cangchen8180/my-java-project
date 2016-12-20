package com.jimi.es.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jimi.es.enums.EsParamTypeEnum;
import com.jimi.es.model.EsParam;
import com.jimi.exception.LgPlatBusinessException;
import com.jimi.utils.LogUtil;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author jimi
 * @description es操作工具类
 * @date 2016-04-09 11:10.
 */
public class EsUtil {

    private static Logger logger = LoggerFactory.getLogger(EsUtil.class);
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            //.serializeNulls() //注：用于设置是否序列化null的字段，开启为序列化，否则null值被忽略
            .create();

    private static String ES_SETTING_PATH_HOME = "path.home";
    private static String ES_SETTING_PATH_HOME_VALUE = "/home/jimi/logstash/elasticsearch-2.2.1";
    private static String ES_SETTING_HTTP_ENABLED = "http.enabled";

    /**
     * 获取单例client
     *
     * @return
     */
    public static Client getSingletonClient() {

        logger.info("[ES][EsUtil]EsUtil loading...");

        return SingletonClient.client;
    }

    /**
     * 获取单例admin client
     *
     * @return
     */
    public static AdminClient getSingletonAdminClient() {

        return SingletonClient.client.admin();
    }

    /**
     * 懒加载单例对象
     */
    private static class SingletonClient {
        /*private static Client client = NodeBuilder.nodeBuilder()
                .settings(Settings.builder()
                        .put(ES_SETTING_PATH_HOME, ES_SETTING_PATH_HOME_VALUE))
                .settings(Settings.settingsBuilder().put(ES_SETTING_HTTP_ENABLED, false))
                .client(true)
                .node().client();*/
        private static Client client;

        //Settings settings = Settings.settingsBuilder()
        //       .put("cluster.name", "molong").build();
        static {
            logger.info("[ES][InnerClass]TransportClient build...");
            try {
                client = TransportClient.builder()
                        //.settings(settings)
                        .build()
                                //java api使用搜索的时候，必须先进行连接，在直接url的时候是端口9200，但是在使用程序的时候为9300
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
            } catch (UnknownHostException e) {
                logger.error("启动ES Client失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询索引
     * @param index 索引名
     * @param type  索引类型名
     * @param paramList 查询字段列表
     * @param offset 开始位置
     * @param limit 结束位置
     * @return
     * @throws LgPlatBusinessException
     */
    public static List<String> queryIndex(String index, String type, List<EsParam> paramList, int offset, int limit) throws LgPlatBusinessException {

        //验证参数
        if (paramsHasNull(index, type)) {
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "queryIndex"}, new String[]{"参数不能含空值"}));
        }
        if (CollectionUtils.isEmpty(paramList)) {
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "queryIndex"}, new String[]{"参数不能含空值"}));
        }

        //构造查询条件-level1
        //todo:有待优化，支持更复杂查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (EsParam param : paramList) {
            EsParamTypeEnum paramType = param.getParamType();
            if (paramType.equals(EsParamTypeEnum.AND)) {
                boolQueryBuilder.must(QueryBuilders.termQuery(param.getField(), param.getValue()));
            } else if (paramType.equals(EsParamTypeEnum.OR)) {
                boolQueryBuilder.should(QueryBuilders.termQuery(param.getField(), param.getValue()));
            } else if (paramType.equals(EsParamTypeEnum.NOT)) {
                boolQueryBuilder.mustNot(QueryBuilders.termQuery(param.getField(), param.getValue()));
            }
        }

        //查询
        SearchResponse searchResponse = getSingletonClient().prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                //使用QueryBuilders.temrQuery()只能对单个汉字可以查出，但多个字旧查不出来的情况原因是：es默认不支持分词，需要装IK插件
                //postFilter:es大部分都是先过滤后聚合，这个可以设置为先聚合后过滤
                //.setPostFilter(QueryBuilders.rangeQuery("userId").from(1).to(25))
                .setFrom(offset).setSize(limit != 0 ? limit : 5000).setExplain(true)
                .execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        List<String> hitList = new ArrayList<>();
        if (null != hits) {
            for (SearchHit hit : hits) {
                hitList.add(hit.getSourceAsString());
            }
        }
        /*SearchHits shs = searchResponse.getHits();
        if (shs.getTotalHits() > 0) {
            for (SearchHit hit : shs) {
                System.out.println("分数:"
                        + hit.getScore()
                        + ", ID:"
                        + hit.getId()
                        + ", customerID:"
                        + hit.getSource().get("id")
                        + ", name:"
                        + hit.getSource().get("name")
                        + ", shortName:"
                        + hit.getSource().get("shortName")
                        + ",联系人："
                        + hit.getSource().get("linkManList"));
            }
        }else{
            logger.info("无符合数据");
        }*/

        return hitList;
    }

    /**
     * 查询指定索引
     *
     * @param index
     * @param type
     * @param id
     * @return
     */
    public static String queryIndex(String index, String type, String id) throws LgPlatBusinessException {

        //验证参数
        if (paramsHasNull(index, type, id)) {
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "queryIndex"}, new String[]{"参数不能含空值"}));
        }

        GetResponse getResponse = getSingletonClient().prepareGet(index, type, id).get();
        logger.info("[ES][queryIndex]responseSource={}", getResponse.getSourceAsString());

        return getResponse.getSourceAsString();
    }

    public static boolean createIndex(String index, String type, String id, String source) throws LgPlatBusinessException{
        //验证参数
        if (paramsHasNull(index, type, source)) {
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "createIndex"}, new String[]{"参数不能含空值"}));
        }
        IndexResponse indexResponse;
        if (!StringUtils.isEmpty(id)) {
            indexResponse = getSingletonClient().prepareIndex()
                    .setIndex(index)
                    .setType(type)
                    .setId(id)
                    .setSource(source)
                    .get();
        }else {
            indexResponse = getSingletonClient().prepareIndex()
                    .setIndex(index)
                    .setType(type)
                    .setSource(source)
                    .get();
        }
        logger.info("[ES][createIndex]response={}", indexResponse.toString());

        return true;
    }

    /**
     * 批量创建索引
     *
     * @param index
     * @param type
     * @param jsonSourceList
     * @return
     * @throws LgPlatBusinessException
     */
    public static boolean bulkCreateIndex(String index, String type, List<String> jsonSourceList) throws LgPlatBusinessException {

        //验证参数
        if (paramsHasNull(index, type)) {
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "bulkCreateIndex"}, new String[]{"参数不能含空值"}));
        }
        if (CollectionUtils.isEmpty(jsonSourceList)) {
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "bulkCreateIndex"}, new String[]{"数据为空，无需操作"}));
        }

        //批量操作
        BulkRequestBuilder bulkRequestBuilder = getSingletonClient().prepareBulk();
        for (String source : jsonSourceList) {
            IndexRequestBuilder indexRequestBuilder = getSingletonClient().prepareIndex(index, type, source);
            bulkRequestBuilder.add(indexRequestBuilder);
        }
        BulkResponse bulkItemResponses =
                bulkRequestBuilder.execute().actionGet();

        boolean result;
        if (bulkItemResponses.hasFailures()) {
            result = false;
            logger.warn("[ES][bulkCreateIndex]批量操作失败，{}", bulkItemResponses.buildFailureMessage());
        } else {
            result = true;
            logger.info("[ES][bulkCreateIndex]批量创建索引完成");
        }

        return result;
    }

    /**
     * 建es索引
     *
     * @param index
     * @param type
     * @param source
     * @return
     */
    public static boolean createIndex(String index, String type, String source) throws LgPlatBusinessException {

        //验证参数
        createIndex(index, type, null, source);

        return true;
    }

    /**
     * 删除索引
     *
     * @param index
     * @param type
     * @param id
     * @return
     */
    public static boolean deleteIndex(String index, String type, String id) throws LgPlatBusinessException {

        //参数不能为空
        if (paramsHasNull(index, type, id)) {
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "deleteIndex"}, new String[]{"参数不能含空值"}));
        }

        DeleteResponse deleteResponse = getSingletonClient().prepareDelete(index, type, id).get();
        logger.info("[ES][createIndex]response={}", deleteResponse.toString());

        return true;
    }

    /**
     * 根据idList批量删除
     *
     * @param index
     * @param type
     * @param idList
     * @return
     * @throws LgPlatBusinessException
     */
    public static boolean bulkDeleteIndex(String index, String type, List<String> idList) throws LgPlatBusinessException {

        //参数不能为空
        if (paramsHasNull(index, type)) {
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "bulkDeleteIndex"}, new String[]{"参数不能含空值"}));
        }
        if (CollectionUtils.isEmpty(idList)) {
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "bulkDeleteIndex"}, new String[]{"数据为空，无需操作"}));
        }

        //批量操作
        BulkRequestBuilder bulkRequestBuilder = getSingletonClient().prepareBulk();
        for (String id : idList) {
            DeleteRequestBuilder deleteRequestBuilder = getSingletonClient().prepareDelete(index, type, id);
            bulkRequestBuilder.add(deleteRequestBuilder);
        }
        BulkResponse bulkItemResponses = bulkRequestBuilder.execute().actionGet();
        boolean result;
        if (bulkItemResponses.hasFailures()) {
            result = false;
            logger.warn("[ES][bulkCreateIndex]批量操作失败，{}", bulkItemResponses.buildFailureMessage());
        } else {
            result = true;
            logger.info("[ES][bulkCreateIndex]批量操作成功");
        }

        return result;
    }

    /**
     * 根据ID选择性更新数据
     * @param index
     * @param type
     * @param id
     * @param name feild名
     * @param value 值
     * @return
     * @throws LgPlatBusinessException
     */
    public static boolean updateIndexSelective(String index, String type, String id, String name, String value) throws LgPlatBusinessException {

        //参数验证
        if (paramsHasNull(index, type, id, name, value)){
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "updateIndexSelective"}, new String[]{"参数不能含空值"}));
        }

        //更新
        Map<String, String> sourceMap = new HashMap<>();
        sourceMap.put(name, value);
        boolean result = updateIndexSelective(index, type, id, sourceMap);

        return result;
    }

    /**
     * 根据ID选择性更新数据
     * @param index
     * @param type
     * @param id
     * @param source 需更新字段数据Map
     * @return
     * @throws LgPlatBusinessException
     */
    public static boolean updateIndexSelective(String index, String type, String id, Map source) throws LgPlatBusinessException {

        //参数验证
        if (paramsHasNull(index, type, id)){
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "updateIndexSelective"}, new String[]{"参数不能含空值"}));
        }
        if (CollectionUtils.isEmpty(source)){
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "updateIndexSelective"}, new String[]{"数据为空，无需操作"}));
        }

        //更新操作
        /*UpdateResponse updateResponse = getSingletonClient().prepareUpdate(index, type, id)
                .setDoc(source)
                .execute().actionGet();*/
        IndexRequest indexRequest = new IndexRequest(index, type, id).source(source);
        UpdateRequest updateRequest = new UpdateRequest(index, type, id)
                .doc(source)
                .upsert(indexRequest);
        try {
            getSingletonClient().update(updateRequest).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        logger.info("[ES][updateIndexSelective]数据更新成功！修改的数据：{}", source.toString());

        return true;
    }

   /**
     * 根据ID选择性更新数据
     * @param index
     * @param type
     * @param id
     * @param obj 需更新字段数据Map
     * @return
     * @throws LgPlatBusinessException
    * 注：**** 空字段为未存入索引，不是索引不存，是gson转换时旧晒掉了
     */
    public static boolean updateIndexSelectiveWithObj(String index, String type, String id, Object obj) throws LgPlatBusinessException {

        //参数验证
        if (paramsHasNull(index, type, id)){
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "updateIndexSelective"}, new String[]{"参数不能含空值"}));
        }

        //更新操作
        /*UpdateResponse updateResponse = getSingletonClient().prepareUpdate(index, type, id)
                .setDoc(source)
                .execute().actionGet();*/
        IndexRequest indexRequest = new IndexRequest(index, type, id).source(gson.toJson(obj));
        UpdateRequest updateRequest = new UpdateRequest(index, type, id)
                .doc(gson.toJson(obj))
                .upsert(indexRequest);
        try {
            getSingletonClient().update(updateRequest).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        logger.info("[ES][updateIndexSelective]数据更新成功！修改的数据：{}", gson.toJson(obj));

        return true;
    }



    /**
     *
     * 测试分词器效果
     * @param index
     * @param text
     * @return
     */
    public static List<String> analyzeTextByAnalyzer(String index, String text) throws LgPlatBusinessException {

        //参数验证
        if (paramsHasNull(index, text)){
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "analyzeTextByAnalyzer"}, new String[]{"index, text不能为空值"}));
        }

        logger.warn("[ES][analyzeTextByAnalyzer]未指定分词器，使用默认分词器");

        List<String> tokenList = analyzeTextByAnalyzer(index, "standard", text);

        return tokenList;
    }

    /**
     *
     * 测试分词器效果
     * @param index
     * @param analyzer
     * @param text
     * @return
     */
    public static List<String> analyzeTextByAnalyzer(String index, String analyzer, String text) throws LgPlatBusinessException {

        //参数验证
        if (paramsHasNull(index, text)){
            throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "analyzeTextByAnalyzer"}, new String[]{"index, text不能为空值"}));
        }

        //指定默认分词器
        if (StringUtils.isEmpty(analyzer)){
            logger.warn("[ES][analyzeTextByAnalyzer]未指定分词器，使用默认分词器");
            analyzer = "standard";
        }

        // Analyzer（分析器）、Tokenizer（分词器）
        //分词
        AnalyzeRequestBuilder analyzeRequestBuilder
                = new AnalyzeRequestBuilder(getSingletonAdminClient().indices(), AnalyzeAction.INSTANCE, index, text)
                .setTokenizer(analyzer);
        AnalyzeResponse analyzeTokens = analyzeRequestBuilder.execute().actionGet();

        List<String> tokenList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(analyzeTokens.getTokens())){
            for (AnalyzeResponse.AnalyzeToken token : analyzeTokens.getTokens()){
                tokenList.add(token.getTerm());
            }
        }

        return tokenList;
    }

    /**
     * es管理客户端
     */
    public static class EsAdminClient{

        /**
         * 获取指定索引的mapping
         * @param index
         * @param type
         * @return
         * @throws LgPlatBusinessException
         */
        public static String getMapping(String index, String type) throws LgPlatBusinessException {

            //参数
            if (paramsHasNull(index, type)){
                throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "AdminClient", "getMapping"}, new String[]{"参数不能为空值"}));
            }

            //执行
            GetMappingsResponse getMappingsResponse = getSingletonAdminClient().indices()
                    .prepareGetMappings(index)
                    .execute().actionGet();

            String mapping = null;
            try {
                mapping = getMappingsResponse.getMappings().get(index).get(type).getSourceAsMap().toString();
            } catch (IOException e) {
                throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "AdminClient", "getMapping"}, new String[]{"getMapping异常"}));
            }

            return mapping;
        }

        /**
         * 设置索引的字段、类型对应的mapping
         * @param index
         * @param type
         * @param mappingBuilder
         * @return
         * @throws LgPlatBusinessException
         */
        public static boolean putMapping(String index, String type, XContentBuilder mappingBuilder) throws LgPlatBusinessException {
            //put-方法一将mapping.json文件直接放在es所在目录的config/mappings下即可
                        //put-方法二
                    /*XContentBuilder crmCustomerMapping = null;
                    try {
                        crmCustomerMapping = XContentFactory.jsonBuilder()
                                .startObject()
                                .startObject("customer")
                                .startObject("properties")
                                .startObject("title").field("type", "string").field("store", "yes").endObject()
                                .startObject("description").field("type", "string").field("index", "not_analyzed").endObject()
                                .startObject("price").field("type", "double").endObject()
                                .startObject("onSale").field("type", "boolean").endObject()
                                .startObject("type").field("type", "integer").endObject()
                                .startObject("createDate").field("type", "date").endObject()
                                .endObject()
                                .endObject()
                                .endObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
            //参数
            if (paramsHasNull(index, type)){
                throw new LgPlatBusinessException(LogUtil.excpetionLogStyle(new String[]{"ES", "AdminClient", "putMapping"}, new String[]{"index, text不能为空值"}));
            }

            //执行
            PutMappingResponse putMappingResponse = getSingletonAdminClient().indices()
                    .preparePutMapping(index)
                    .setType(type)
                    .setSource(mappingBuilder)
                    .execute().actionGet();
            logger.info("putMappingResponse=", putMappingResponse.toString());

            return true;
        }
    }

    /**
     * 判断参数列表不能为空
     *
     * @param params
     * @return
     */
    private static boolean paramsHasNull(String... params) {

        if (null == params) {
            return true;
        }

        for (int i = 0; i < params.length; i++) {
            if (StringUtils.isEmpty(params[i])) {
                return true;
            }
        }

        return false;
    }
}
