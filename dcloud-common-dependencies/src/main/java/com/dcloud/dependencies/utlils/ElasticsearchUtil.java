package com.dcloud.dependencies.utlils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * ElasticSearch 工具类
 *
 * @author dcloud
 * @version es: 6.3.1
 * @date 2021-12-17 16:00
 */
@Slf4j
public class ElasticsearchUtil {

    private RestHighLevelClient restHighLevelClient;

    public ElasticsearchUtil(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Resource
    private RedisUtil redisUtil;

    /**
     * 获取 RestHighLevelClient 对象
     *
     * @return RestHighLevelClient 对象
     */
    public RestHighLevelClient getRestHighLevelClient() {
        return this.restHighLevelClient;
    }

    // ========================== index manage ================================

    /**
     * 创建一个索引
     * NOTE: 强烈不推荐使用这种方式创建索引，建议使用kibana/head工具创建
     * 索引和mapping的参数设置参考：https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.3/java-rest-high-create-index.html
     *
     * @param indexName      索引名
     * @param indexSetting   索引参数设置
     *                       Map("index.number_of_shards" -> "shardNum",
     *                       "index.number_of_replicas" -> "replicationNum")
     * @param mappingSetting 索引mapping块的设置
     * @param alias          索引别名
     * @return true/false
     */
    public boolean createIndex(String indexName, Map<String, String> indexSetting, Map<String, Object> mappingSetting, String alias) {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        boolean exist = indexIsExist(indexName);
        boolean createSuccess = false;
        // 判断索引是否存在
        if (exist) {
            log.warn("## Index [{}] already exist!", indexName);
            return createSuccess;
        }
        request.timeout(TimeValue.timeValueMinutes(2));
        // 添加分片和备份参数
        if (indexSetting != null) {
            request.settings(Settings.builder()
                    .put("index.number_of_shards", indexSetting.get("index.number_of_shards"))
                    .put("index.number_of_replicas", indexSetting.get("index.number_of_replicas"))
            );
        }
        // 设置mapping属性
        if (indexSetting != null) {
            request.mapping("_doc", mappingSetting);
        }
        // 添加别名
        if (alias != null) {
            request.alias(new Alias(alias));
        }
        try {
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request);
            createSuccess = createIndexResponse.isAcknowledged();
            log.info("## [{}] has been created successfully!", indexName);
        } catch (IOException e) {
            log.error("## Create index occur error: [{}]" + e.getMessage());
        }
        return createSuccess;
    }

    /**
     * 删除一个已经存在的索引
     *
     * @param indexName 索引名
     * @return true/false
     */
    public boolean deleteIndex(String indexName) {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        request.timeout(TimeValue.timeValueMinutes(2));
        boolean exist = indexIsExist(indexName);
        boolean deleteSuccess = false;
        // 判断索引是否存在
        if (!exist) {
            log.warn("## Index [{}] isn't existence!", indexName);
            return deleteSuccess;
        }
        try {
            DeleteIndexResponse response = restHighLevelClient.indices().delete(request);
            deleteSuccess = response.isAcknowledged();
        } catch (IOException e) {
            log.error("## Deleting index occur error: [{}]" + e.getMessage());
        }
        return deleteSuccess;
    }

    /**
     * 判断索引是否存在
     *
     * @param indexName 索引名
     * @return true/false
     */
    public boolean indexIsExist(String indexName) {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(indexName);
        boolean exists = false;
        // 使用synchronous(同步)的方式
        try {
            exists = restHighLevelClient.indices().exists(request);
        } catch (IOException e) {
            log.error("## Get index info occur error: [{}]" + e.getMessage());
        }
        return exists;
    }

    /**
     * 判断 doc 是否存在
     *
     * @param indexName 索引名
     * @param type      类型
     * @param docId     数据 _id
     * @return 执行结果
     */
    public boolean docIsExist(String indexName, String type, String docId) {
        GetRequest request = new GetRequest(indexName, type, docId);
        // disable fetching _source
        request.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
        // disable fetching stored fields
        request.storedFields("_none_");

        boolean exists = false;
        try {
            exists = restHighLevelClient.exists(request);
        } catch (IOException e) {
            log.error("## 查询 doc 是否存在时发送异常，index: {}, type: {}, docId: {}, 错误信息：{}", indexName, type, docId, e);
        }
        return exists;
    }

    /**
     * 根据 es doc _id 获取文档
     *
     * @param indexName 索引名
     * @param type      类型
     * @param docId     数据 _id
     * @param fetchFields   返回字段
     * @param fetchAllFields    是否返回全部字段，如是，则 fetchFields 参数无效
     * @return  文档
     */
    public Map<String, Object> getDocById(String indexName, String type, String docId, String[] fetchFields, boolean fetchAllFields) {
        GetRequest request = new GetRequest(indexName, type, docId);

        if (!fetchAllFields) {
            request.fetchSourceContext(new FetchSourceContext(true, fetchFields, Strings.EMPTY_ARRAY));
        }

        try {
            GetResponse response = restHighLevelClient.get(request);
            if (response.isExists()) {
                return response.getSourceAsMap();
            } else {
                // doc not found
                return new HashMap<String, Object>();
            }
        } catch (IOException e) {
            log.error("## 根据文档 id 获取文档时出现异常，index: {}, type: {}, docId: {}, 错误信息：{}", indexName, type, docId, e);
            return new HashMap<String, Object>();
        }
    }

    // ===================== doc insert/update/delete =========================

    /**
     * 插入文档内容
     *
     * @param indexName 索引名
     * @param type      类型
     * @param docId     数据 _id
     * @param jsonMap   要插入的字段和值
     * @return 执行结果
     */
    public boolean insertDataWithId(String indexName, String type, String docId, Map<String, Object> jsonMap) {
        IndexRequest request = new IndexRequest(indexName, type, docId);
        request.source(jsonMap)
                .timeout(TimeValue.timeValueMinutes(1L))
                .opType("create");
        boolean success = false;
        try {
            IndexResponse response = restHighLevelClient.index(request);
            success = true;
        } catch (IOException e) {
            log.error("## 数据: [{}], 插入es索引: [{}] 时发生错误: [{}]", jsonMap.toString(), indexName, e);
        }
        return success;
    }

    // update

    /**
     * 根据docId更新字段内容
     *
     * @param indexName 索引名
     * @param type      类型
     * @param docId     数据 _id
     * @param jsonMap   要更新的字段和值
     * @return 执行结果
     */
    public boolean updateById(String indexName, String type, String docId, Map<String, Object> jsonMap) {
        boolean success = false;
        try {
            UpdateRequest request = new UpdateRequest(indexName, type, docId);
            request.doc(jsonMap);
            UpdateResponse response = restHighLevelClient.update(request);
            success = true;
        } catch (Exception e) {
            log.error("## Update index: [{}] occur error: [{}].", indexName, e);
        }
        return success;
    }

    /**
     * 根据 docId 为指定字段加指定值
     *
     * <p>
     * WARNING: es 索引字段的类型为 long，且索引 mapping 的 "dynamic" 属性设置为 "strict" 模式,
     * 否则可能出现 888-3 这样的数据, 而理想的结果是 885。
     * </p>
     *
     * @param indexName 索引名
     * @param type      类型
     * @param docId     数据 _id
     * @param map       ("sales", 10L) or ("sales", -10L)
     * @return 执行结果
     */
    public boolean incrByDocId(String indexName, String type, String docId, Map<String, Long> map) throws ElasticsearchStatusException {
        boolean success = false;
        StringBuilder updateScript = new StringBuilder();
        try {
            UpdateRequest updateRequest = new UpdateRequest(indexName, type, docId);
            for (String field : map.keySet()) {
                updateScript.append("ctx._source.").append(field).append("= ctx._source.").append(field).append(" + ").append(map.get(field)).append(";");
            }
            Script inline = new Script(updateScript.toString());
            updateRequest.script(inline);

            updateRequest.timeout(TimeValue.timeValueMinutes(3));
            updateRequest.retryOnConflict(3);

            restHighLevelClient.update(updateRequest);
            success = true;
        } catch (ElasticsearchStatusException e) {
            throw new ElasticsearchStatusException("## es更新脚本执行错误，执行脚本：" + updateScript, RestStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("## Update index: [{}] occur error: [{}].", indexName, e);
        }
        return success;
    }

    /**
     * 根据 docId 为指定字段加其它字段的值，如 sales = fake_sales + 10
     *
     * <p>
     * WARNING: es 索引字段的类型为 long，且索引 mapping 的 "dynamic" 属性设置为 "strict" 模式,
     * 否则可能出现 888-3 这样的数据, 而理想的结果是 885。
     * </p>
     *
     * @param indexName    索引名
     * @param type         类型
     * @param docId        数据 _id
     * @param map          ("sales", 10L)
     * @param otherField   需要相加的字段
     * @param customScript 自定义 painless 脚本
     * @return 执行结果
     */
    public boolean incrWithOtherField(String indexName, String type, String docId, Map<String, Long> map, String otherField, @Nullable String customScript) {
        boolean success = false;
        try {
            UpdateRequest updateRequest = new UpdateRequest(indexName, type, docId);
            StringBuilder updateScript = new StringBuilder();
            for (String field : map.keySet()) {
                updateScript.append("ctx._source.").append(field).append("= ctx._source.").append(otherField).append(" + ").append(map.get(field)).append(";");
            }
            // 添加自定义 painless 脚本
            updateScript.append(customScript);
            Script inline = new Script(updateScript.toString());
            updateRequest.script(inline);

            updateRequest.timeout(TimeValue.timeValueMinutes(3));
            updateRequest.retryOnConflict(3);

            restHighLevelClient.update(updateRequest);
            success = true;
        } catch (Exception e) {
            log.error("## Update index: [{}] occur error: [{}].", indexName, e);
        }
        return success;
    }

    /**
     * 根据docId更新/插入字段内容
     *
     * @param indexName 索引名
     * @param type      类型
     * @param docId     数据 _id
     * @param jsonMap   要更新/插入的字段和值
     * @return 执行结果
     */
    public boolean upsertById(String indexName, String type, String docId, Map<String, Object> jsonMap) {
        boolean success = false;
        try {
            // 更新插入
            UpdateRequest request = new UpdateRequest(indexName, type, docId)
                    .doc(jsonMap)
                    .upsert(jsonMap);
            UpdateResponse response = restHighLevelClient.update(request);
            success = true;
        } catch (IOException e) {
            log.error("## Upsert index [{}] occur error: [{}].", indexName, e);
        }
        return success;
    }

    /**
     * 批量更新 es doc
     *
     * @param indexName 索引名
     * @param type      类型
     * @param docIdName 数据主键字段名：如专辑ID：albumId
     * @param updateDocs    待更新内容 {“albumId”："aaa","videosTitle":"videoName1|videoName2"}
     * @return  操作结果
     */
    public boolean bulkUpdate(String indexName, String type, String docIdName,List<Map<String, String>> updateDocs) {
        boolean success = false;
        try {
            if (CollectionUtils.isNotEmpty(updateDocs)) {
                BulkRequest bulkRequest = new BulkRequest();
                updateDocs.forEach(
                        updateDoc ->
                                bulkRequest.add(new UpdateRequest(indexName, type, updateDoc.get(docIdName)).doc(updateDoc))
                );

                bulkRequest.timeout(TimeValue.timeValueMinutes(3L));

                BulkResponse response = restHighLevelClient.bulk(bulkRequest);
                if (response.hasFailures()){
                    String failureMessage = response.buildFailureMessage();
                    log.error("## 批量更新 {} 索引失败：{}", indexName, failureMessage);
                } else {
                    success = true;
                }
            }
        } catch (IOException e) {
            log.error("## 批量更新 {} 索引出现异常：{}， {}，", indexName, e.getMessage(), e);
        }
        return success;
    }

    // ========================= common search ================================

    /**
     * 多字段 or 条件查询，分页返回
     * select field1, field2 from index where field1 = 'laipan' or field2 = 'laipan' ...
     */
    public EsPage<Map<String, Object>> searchWithOr(String indexName, String type, String searchContent, String[] searchFields, String[] returnFields, Map<String, String> sortMap, String[] highlightFields, int pageNum, int pageSize) {
        BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.multiMatchQuery(searchContent, searchFields));
        return searchDataPage(indexName, type, query, returnFields, sortMap, highlightFields, pageNum, pageSize);
    }

    /**
     * 先过滤，然后多字段 or 条件查询，分页返回
     * select field1, field2 from index where field1 != 'falcon' and field2 = 'laipan' or field3 = 'laipan' ...
     */
    public EsPage<Map<String, Object>> searchWithOrByFilter(String indexName, String type, Map<String, String> filterMap, String searchContent, String[] searchFields, String[] returnFields, Map<String, String> sortMap, String[] highlightFields, int pageNum, int pageSize) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (String field : filterMap.keySet()) {
            query.filter(QueryBuilders.matchQuery(field, filterMap.get(field)));
        }
        query.must(QueryBuilders.multiMatchQuery(searchContent, searchFields));
        return searchDataPage(indexName, type, query, returnFields, sortMap, highlightFields, pageNum, pageSize);
    }

    /**
     * 先过滤，然后多字段 or 条件查询，分页返回
     * select field1, field2 from index where field1 != 'falcon' and field2 = 'laipan' or field3 = 'laipan' ...
     */
    public <T> EsPage<T> searchWithOrByFilter(String indexName, String type, Map<String, String> filterMap, String searchContent, String[] searchFields, String[] returnFields, Map<String, String> sortMap, String[] highlightFields, int pageNum, int pageSize, Class<T> clazz) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (String field : filterMap.keySet()) {
            query.filter(QueryBuilders.matchQuery(field, filterMap.get(field)));
        }
        query.must(QueryBuilders.multiMatchQuery(searchContent, searchFields));
        return searchDataPage(indexName, type, query, returnFields, sortMap, highlightFields, pageNum, pageSize, clazz);
    }

    /**
     * 多字段 and 条件查询，分页返回
     * select field1, field2 from index where field1 = 'laipan1' and field2 = 'laipan2' ...
     */
    public EsPage<Map<String, Object>> searchWithAnd(String indexName, String type, Map<String, String> queryMap, String[] returnFields, Map<String, String> sortMap, String[] highlightFields, int pageNum, int pageSize) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        Set<String> queryFields = queryMap.keySet();
        for (String queryField : queryFields) {
            query.must(QueryBuilders.matchQuery(queryField, queryMap.get(queryField)));
        }
        return searchDataPage(indexName, type, query, returnFields, sortMap, highlightFields, pageNum, pageSize);
    }

    /**
     * 多字段 and 条件查询，分页返回
     * select field1, field2 from index where field1 = 'laipan1' and field2 = 'laipan2' ...
     */
    public <T> EsPage<T> searchWithAnd(String indexName, String type, Map<String, String> queryMap, String[] returnFields, Map<String, String> sortMap, String[] highlightFields, int pageNum, int pageSize, Class<T> clazz) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        Set<String> queryFields = queryMap.keySet();
        for (String queryField : queryFields) {
            query.must(QueryBuilders.matchQuery(queryField, queryMap.get(queryField)));
        }
        return searchDataPage(indexName, type, query, returnFields, sortMap, highlightFields, pageNum, pageSize, clazz);
    }

    /**
     * 先过滤，然后多字段 and 条件查询，分页返回
     * select field1, field2 from index where field1 != 'falcon' field2 = 'laipan1' and field3 = 'laipan2' ...
     */
    public EsPage<Map<String, Object>> searchWithAndByFilter(String indexName, String type, Map<String, String> filterMap, Map<String, String> queryMap, String[] returnFields, Map<String, String> sortMap, String[] highlightFields, int pageNum, int pageSize) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (String field : filterMap.keySet()) {
            query.filter(QueryBuilders.matchQuery(field, filterMap.get(field)));
        }
        for (String queryField : queryMap.keySet()) {
            query.must(QueryBuilders.matchQuery(queryField, queryMap.get(queryField)));
        }
        return searchDataPage(indexName, type, query, returnFields, sortMap, highlightFields, pageNum, pageSize);
    }

    /**
     * 先过滤，然后多字段 and 条件查询，分页返回
     * select field1, field2 from index where field1 != 'falcon' field2 = 'laipan1' and field3 = 'laipan2' ...
     */
    public <T> EsPage<T> searchWithAndByFilter(String indexName, String type, Map<String, String> filterMap, Map<String, String> queryMap, String[] returnFields, Map<String, String> sortMap, String[] highlightFields, int pageNum, int pageSize, Class<T> clazz) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (String field : filterMap.keySet()) {
            query.filter(QueryBuilders.matchQuery(field, filterMap.get(field)));
        }
        for (String queryField : queryMap.keySet()) {
            query.must(QueryBuilders.matchQuery(queryField, queryMap.get(queryField)));
        }
        return searchDataPage(indexName, type, query, returnFields, sortMap, highlightFields, pageNum, pageSize, clazz);
    }


    /**
     * 多字段 and 条件查询，list 返回
     * select field1, field2 from index where field1 = 'laipan1' and field2 = 'laipan2' ...
     */
    public List<Map<String, Object>> searchListWithAnd(String indexName, String type, Map<String, String> queryMap, List<Integer> searchRange, String[] returnFields, Map<String, String> sortMap, List<String> highlightFields) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (String queryField : queryMap.keySet()) {
            query.must(QueryBuilders.matchQuery(queryField, queryMap.get(queryField)));
        }
        return searchDataList(indexName, type, query, searchRange, returnFields, sortMap, highlightFields);
    }

    // ======================================== geo search ===================================

    /**
     * 先过滤，然后多字段 and 条件查询，按距离升序，分页返回
     * select field1, field2 from index where field1 != 'xxx' field2 = 'xxx' and field3 = 'xxx' ...
     */
    public <T> EsPage<T> searchDistWithAndByFilter(String indexName, String type, Map<String, String> filterMap, Map<String, String> queryMap, String[] returnFields, String geoFiled, String latitude, String longitude, String distanceField, String[] highlightFields, int pageNum, int pageSize, Class<T> clazz) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (String field : filterMap.keySet()) {
            query.filter(QueryBuilders.matchQuery(field, filterMap.get(field)));
        }
        for (String queryField : queryMap.keySet()) {
            query.must(QueryBuilders.matchQuery(queryField, queryMap.get(queryField)));
        }
        return searchDataPageWithPTPDist(indexName, type, query, returnFields, geoFiled, latitude, longitude, distanceField, highlightFields, pageNum, pageSize, clazz);
    }

    /**
     * 返回对象集合
     */
    public <T> List<T> searchDataList(String indexName, String type, QueryBuilder query, List<Integer> searchRange, String[] returnFields, Map<String, String> sortMap, List<String> highlightFields, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        if (searchRange == null) {
            //没设置range默认为5000,修复默认10条的问题
            searchRange = new ArrayList<>();
            searchRange.add(0);
            searchRange.add(5000);
        }
        List<Map<String, Object>> maps = searchDataList(indexName, type, query, searchRange, returnFields, sortMap, highlightFields);
        for (Map<String, Object> map :
                maps) {
            list.add(JSONObject.parseObject(JSONObject.toJSONString(map), clazz));
        }
        return list;
    }

    /**
     * 数据查询，以List返回数据
     *
     * @param indexName       索引名
     * @param type            类型名
     * @param query           查询条件
     * @param searchRange     查询范围，Nullable，为 null 时默认返回10条
     *                        searchRange["0", "8"]
     * @param returnFields    返回字段， Nullable，为 null 时返回全部字段
     *                        String[] returnFields = {"field1", "field2"}
     * @param sortMap         排序字段及排序方式: asc 升序 / desc 降序
     *                        sortMap("field1" -> "asc", "field2" -> "desc")
     * @param highlightFields 高亮字段，Nullable，为 null 时没有高亮
     *                        List returnFields = [{]"field1", "field2"]
     * @return 符合条件的结果集
     */
    public List<Map<String, Object>> searchDataList(String indexName, String type, QueryBuilder query, List<Integer> searchRange, String[] returnFields, Map<String, String> sortMap, List<String> highlightFields) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.timeout(TimeValue.timeValueMinutes(3));
        // 设置查询条件
        builder.query(query);

        // 设置返回结果集条数
        if (searchRange != null) {
            if (searchRange.size() > 0) {
                builder.from(searchRange.get(0));
                builder.size(searchRange.get(1));
            }
        }

        // 设置返回的结果字段
        if (returnFields != null) {
            builder.fetchSource(returnFields, null);
        }

        // 排序
        if (sortMap != null) {
            for (String sortField : sortMap.keySet()) {
                builder.sort(sortField, "asc".equals(sortMap.get(sortField)) ? SortOrder.ASC : SortOrder.DESC);
            }
        }

        // 设置高亮字段
        if (highlightFields != null) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            for (String hf : highlightFields) {
                highlightBuilder.field(hf);
            }
            builder.highlighter(highlightBuilder);
        }

        // 记录查询语句，方便测试
        log.info("## Query index [{}] with DSL {}", indexName, builder);

        // 完成查询条件设置
        searchRequest.source(builder);

        List<Map<String, Object>> list = new ArrayList<>();
        // 获取查询结果
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            // 判断查询结果是否超时
            boolean timedOut = searchResponse.isTimedOut();
            if (timedOut) {
                log.error("## Search result from [{}] is timeout!", indexName);
            }
            // 获取查询结果
            SearchHits hits = searchResponse.getHits();
            // 记录查询结果条数
            long totalHits = hits.totalHits;
            log.info("## Search result total have [{}]", totalHits);
            // 遍历结果集，封装
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                list.add(hit.getSourceAsMap());
            }
            return list;
        } catch (IOException e) {
            log.error("## Get search results occur error: {}, error info: {}", e.getMessage(), e);
        }
        return list;
    }


    /**
     * 数据查询，以分页形式放回
     *
     * @param indexName       索引名
     * @param type            类型名
     * @param query           查询条件
     * @param returnFields    返回字段
     *                        String[] returnFields = {"field1", "field2"}
     * @param sortMap         排序字段及排序方式: asc 升序 / desc 降序
     *                        sortMap("field1" -> "asc", "field2" -> "desc")
     * @param highlightFields 高亮字段
     *                        List returnFields = [{]"field1", "field2"]
     * @param pageNum         当前页码
     * @param pageSize        页面大小
     * @return 符合条件的结果集
     */
    public EsPage<Map<String, Object>> searchDataPage(String indexName, String type, QueryBuilder query, String[] returnFields, Map<String, String> sortMap, String[] highlightFields, int pageNum, int pageSize) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.timeout(TimeValue.timeValueMinutes(3));
        // 设置查询条件
        builder.query(query);

        // 设置分页
        builder.from((pageNum - 1) * pageSize);
        builder.size(pageSize);

        // 设置返回的结果字段
        if (returnFields != null) {
            builder.fetchSource(returnFields, null);
        }

        // 排序
        if (sortMap != null) {
            for (String sortField : sortMap.keySet()) {
                builder.sort(sortField, "asc".equals(sortMap.get(sortField)) ? SortOrder.ASC : SortOrder.DESC);
            }
        }

        // 设置高亮字段
        if (highlightFields != null) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            for (String hf : highlightFields) {
                highlightBuilder.field(hf);
            }
            builder.highlighter(highlightBuilder);
        }

        // 记录查询语句，方便测试
        log.info("## Query index [{}] with DSL {}", indexName, builder);

        // 完成查询条件设置
        searchRequest.source(builder);

        List<Map<String, Object>> list = new ArrayList<>();
        // 获取查询结果
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            // 判断查询结果是否超时
            boolean timedOut = searchResponse.isTimedOut();
            if (timedOut) {
                log.error("## Search result from [{}] is timeout!", indexName);
            }
            // 获取查询结果
            if (searchResponse.status().getStatus() == 200) {
                SearchHits hits = searchResponse.getHits();
                // 记录查询结果条数
                long totalHits = hits.totalHits;
                log.info("## Search result total have [{}]", totalHits);
                // 遍历结果集，封装
                SearchHit[] searchHits = hits.getHits();
                for (SearchHit hit : searchHits) {
                    list.add(hit.getSourceAsMap());
                }
                return new EsPage<Map<String, Object>>(pageNum, pageSize, (int) totalHits, list);
            }
        } catch (IOException e) {
            log.error("## Get search results occur error: {}, error info: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 数据查询，以分页形式放回
     *
     * @param indexName       索引名
     * @param type            类型名
     * @param query           查询条件
     * @param returnFields    返回字段
     *                        String[] returnFields = {"field1", "field2"}
     * @param sortMap         排序字段及排序方式: asc 升序 / desc 降序
     *                        sortMap("field1" -> "asc", "field2" -> "desc")
     * @param highlightFields 高亮字段
     *                        List returnFields = [{]"field1", "field2"]
     * @param pageNum         当前页码
     * @param pageSize        页面大小
     * @param clazz           DTO类
     * @return 符合条件的结果集
     */
    public <T> EsPage<T> searchDataPage(String indexName, String type, QueryBuilder query, String[] returnFields, Map<String, String> sortMap, String[] highlightFields, int pageNum, int pageSize, Class<T> clazz) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.timeout(TimeValue.timeValueMinutes(3));
        // 设置查询条件
        builder.query(query);

        // 设置分页
        builder.from((pageNum - 1) * pageSize);
        builder.size(pageSize);


        // 设置返回的结果字段
        if (returnFields != null) {
            builder.fetchSource(returnFields, null);
        }

        // 排序
        if (sortMap != null) {
            for (String sortField : sortMap.keySet()) {
                builder.sort(sortField, "asc".equals(sortMap.get(sortField)) ? SortOrder.ASC : SortOrder.DESC);
            }
        }

        // 设置高亮字段
        if (highlightFields != null) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            for (String hf : highlightFields) {
                highlightBuilder.field(hf);
            }
            builder.highlighter(highlightBuilder);
        }

        // 记录查询语句，方便测试
        log.info("## Query index [{}] with DSL {}", indexName, builder);

        // 完成查询条件设置
        searchRequest.source(builder);

        List<T> list = new ArrayList<>();
        // 获取查询结果
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            // 判断查询结果是否超时
            boolean timedOut = searchResponse.isTimedOut();
            if (timedOut) {
                log.error("## Search result from [{}] is timeout!", indexName);
            }
            // 获取查询结果
            if (searchResponse.status().getStatus() == 200) {
                SearchHits hits = searchResponse.getHits();
                // 记录查询结果条数
                long totalHits = hits.totalHits;
                log.info("## Search result total have [{}]", totalHits);
                // 遍历结果集，封装
                SearchHit[] searchHits = hits.getHits();
                for (SearchHit hit : searchHits) {
                    list.add(JSONObject.parseObject(hit.getSourceAsString(), clazz));
                }
                return new EsPage<T>(pageNum, pageSize, (int) totalHits, list);
            }
        } catch (IOException e) {
            log.error("## Get search results occur error: {}, error info: {}", e.getMessage(), e);
        }
        return null;
    }

    // ========================= aggregation search ==================================
    /**
     * 先过滤，然后按条件查询，最后按指定字段求 count（！不分组）
     * select count(1) as countField from indexName where field1 != xxx and field2 = xxx and field3 = xxx
     *
     * @param indexName       索引名
     * @param type            类型名
     * @param filterMap       过滤条件
     * @param queryMap        查询条件
     * @param termField       分组字段
     * @return count结果
     */
    public long aggCountWithAndByFilter(String indexName, String type, Map<String, String> filterMap, Map<String, String> queryMap, String termField) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (String field : filterMap.keySet()) {
            query.filter(QueryBuilders.matchQuery(field, filterMap.get(field)));
        }
        for (String queryField : queryMap.keySet()) {
            query.must(QueryBuilders.matchQuery(queryField, queryMap.get(queryField)));
        }
        return aggCount(indexName, type, query, termField);
    }

    /**
     * 先过滤，然后按条件查询，最后按指定字段分组求 count
     * select count(1) as countField from indexName where field1 != xxx and field2 = xxx and field3 = xxx group by termField
     *
     * @param indexName      索引名
     * @param type           类型名
     * @param filterMap      过滤条件
     * @param queryMap       查询条件
     * @param termField      分组字段
     * @param keyFieldName   分组后各组 key 的字段名
     * @param countFieldName count 结果的字段名
     * @return count结果 [map1(("keyFieldName" -> "key"),("countFieldName" -> "countResult"); map2(...)]
     */
    public List<Map<String, String>> aggTermCountWithAndByFilter(String indexName, String type, @Nullable Map<String, String> filterMap, @Nullable Map<String, String> queryMap, String termField, String keyFieldName, String countFieldName) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (filterMap != null) {
            for (String field : filterMap.keySet()) {
                query.filter(QueryBuilders.matchQuery(field, filterMap.get(field)));
            }
        }
        if (queryMap != null) {
            for (String queryField : queryMap.keySet()) {
                query.must(QueryBuilders.matchQuery(queryField, queryMap.get(queryField)));
            }
        }
        return aggTermCount(indexName, type, query, termField, keyFieldName, countFieldName);
    }

    /**
     * 聚合求 count
     *
     * @param indexName 索引名
     * @param type      类型名
     * @param query     过滤条件
     * @param termField 查询条件
     * @return count结果
     */
    public long aggCount(String indexName, String type, QueryBuilder query, String termField) {
        // 处理查询和过滤条件
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.timeout(TimeValue.timeValueMinutes(3));
        // 设置查询条件
        builder.query(query);

        ValueCountAggregationBuilder countAggregationBuilder = AggregationBuilders
                .count("count")
                .field(termField);
        builder.aggregation(countAggregationBuilder);

        // 完成查询条件设置
        searchRequest.source(builder);
        // 记录查询语句，方便测试
        log.info("## Query index [{}] with DSL {}", indexName, builder);

        long res = 0;
        // 获取查询结果
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            // 判断查询结果是否超时
            boolean timedOut = searchResponse.isTimedOut();
            if (timedOut) {
                log.error("## Search result from [{}] is timeout!", indexName);
            }
            // 获取查询结果
            if (searchResponse.status().getStatus() == 200) {
                ValueCount agg = searchResponse.getAggregations().get("count");
                res = agg.getValue();
            }
        } catch (IOException e) {
            log.error("## Get search results occur error: {}, error info: {}", e.getMessage(), e);
        }
        return res;
    }

    /**
     * 分组求 count
     *
     * @param indexName      索引名
     * @param type           类型名
     * @param query          过滤条件
     * @param termField      查询条件
     * @param keyFieldName   分组后各组 key 的字段名
     * @param countFieldName count 结果的字段名
     * @return count结果 [map1(("keyFieldName" -> "key"),("countFieldName" -> "countResult"); map2(...)]
     */
    public List<Map<String, String>> aggTermCount(String indexName, String type, QueryBuilder query, String termField, String keyFieldName, String countFieldName) {
        // 处理查询和过滤条件
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.timeout(TimeValue.timeValueMinutes(3));
        // 设置查询条件
        builder.query(query);

        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders
                .terms("terms")
                .field(termField);
        builder.aggregation(termsAggregationBuilder);

        // 完成查询条件设置
        searchRequest.source(builder);
        // 记录查询语句，方便测试
        log.info("## Query index [{}] with DSL {}", indexName, builder);

        List<Map<String, String>> list = new ArrayList<>();
        // 获取查询结果
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            // 判断查询结果是否超时
            boolean timedOut = searchResponse.isTimedOut();
            if (timedOut) {
                log.error("## Search result from [{}] is timeout!", indexName);
            }
            // 获取查询结果
            if (searchResponse.status().getStatus() == 200) {
                Terms terms = searchResponse.getAggregations().get("terms");
                for (Terms.Bucket entry : terms.getBuckets()) {
                    Map<String, String> map = new HashMap<>();
                    map.put(keyFieldName, entry.getKey().toString());
                    map.put(countFieldName, Long.toString(entry.getDocCount()));
                    list.add(map);
                }
            }
        } catch (IOException e) {
            log.error("## Get search results occur error: {}, error info: {}", e.getMessage(), e);
        }
        return list;
    }

    /**
     * 获取count 值
     * select count(countfiled) as countName from indexName/type where searchField = searchContent
     *
     * @param indexName     索引名
     * @param type          type名
     * @param searchField   查询字段
     * @param searchContent 查询内容
     * @param countName     聚合名称
     * @param countfiled    聚合字段
     * @return count值
     */
    public long countAggregate(String indexName, String type, String searchField, String searchContent, String countName, String countfiled) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);

        // 设置聚合查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(searchField, searchContent);

        ValueCountAggregationBuilder aggregation = AggregationBuilders
                .count(countName)
                .field(countfiled);

        // 添加查询条件
        builder.query(matchQueryBuilder);
        builder.aggregation(aggregation);
        // 获取 count 结果
        searchRequest.source(builder);

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest);
            ValueCount agg = response.getAggregations().get(countName);
            return agg.getValue();
        } catch (Exception e) {
            log.error("## Get search results occur error: {}, error info: {}", e.getMessage(), e);
            return 0L;
        }
    }

    // ============================= geo search ======================================

    /**
     * 获取点到文档之间的距离，按距离升序
     * 注意：1. Class 中距离字段与 “自定义返回的距离字段名” 相同
     *      2. Class 中距离字段类型为 String
     *
     * @param indexName       索引名
     * @param type            类型名
     * @param query           查询条件
     * @param returnFields    返回字段
     *                        String[] returnFields = {"field1", "field2"}
     * @param geoFiled        es geo_point 字段
     * @param latitude        维度
     * @param longitude       经度
     * @param distanceField   自定义返回的距离字段名
     * @param highlightFields 高亮字段，Nullable，为 null 时没有高亮
     *                        List highlightFields = [{]"field1", "field2"]
     * @param pageNum         当前页码
     * @param pageSize        页面大小
     * @return 符合条件的结果集
     */
    public <T> EsPage<T> searchDataPageWithPTPDist(String indexName, String type, QueryBuilder query, String[] returnFields, String geoFiled, String latitude, String longitude, String distanceField, String[] highlightFields, int pageNum, int pageSize, Class<T> clazz) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.timeout(TimeValue.timeValueMinutes(3));
        // 设置查询条件
        builder.query(query);

        // 设置分页
        builder.from((pageNum - 1) * pageSize);
        builder.size(pageSize);

        // 设置返回的结果字段
        if (returnFields != null) {
            builder.fetchSource(returnFields, null);
        }

        // 判断是否获取到用户定位
        if (StringUtils.isNotBlank(longitude) && StringUtils.isNotBlank(latitude)) {
            // 按距离排序
            GeoDistanceSortBuilder distanceSort = new GeoDistanceSortBuilder(geoFiled, Double.parseDouble(latitude), Double.parseDouble(longitude));
            // 以 m 为单位
            distanceSort.unit(DistanceUnit.METERS);
            // 升序
            distanceSort.order(SortOrder.ASC);
            // 以最快的 geo 计算方式获取距离
            distanceSort.geoDistance(GeoDistance.PLANE);
            builder.sort(distanceSort);
        }

        // 设置高亮字段
        if (highlightFields != null) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            for (String hf : highlightFields) {
                highlightBuilder.field(hf);
            }
            builder.highlighter(highlightBuilder);
        }

        // 记录查询语句，方便测试
        log.info("## Query index [{}] with DSL {}", indexName, builder);

        // 完成查询条件设置
        searchRequest.source(builder);

        List<T> list = new ArrayList<>();
        // 获取查询结果
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            // 判断查询结果是否超时
            boolean timedOut = searchResponse.isTimedOut();
            if (timedOut) {
                log.error("## Search result from [{}] is timeout!", indexName);
            }
            // 获取查询结果
            if (searchResponse.status().getStatus() == 200) {
                SearchHits hits = searchResponse.getHits();
                // 记录查询结果条数
                long totalHits = hits.totalHits;
                log.info("## Search result total have [{}]", totalHits);
                // 遍历结果集，封装
                SearchHit[] searchHits = hits.getHits();
                for (SearchHit hit : searchHits) {
                    T obj = JSONObject.parseObject(hit.getSourceAsString(), clazz);
                    // 判断是否获取到用户定位
                    if (StringUtils.isNotBlank(longitude) && StringUtils.isNotBlank(latitude)) {
                        // 在结果中插入距离
                        FieldUtils.writeField(obj, distanceField, hit.getSortValues()[0].toString(), true);
                    }
                    // 封装结果
                    list.add(obj);
                }
                return new EsPage<T>(pageNum, pageSize, (int) totalHits, list);
            }
        } catch (Exception e) {
            log.error("## Get search results occur error: {}, error info: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取直方图
     * select count(countfiled) as countName from indexName/type where searchField = searchContent
     *
     * @param indexName 索引名
     * @param type      type名
     * @param timeField 查询时间字段
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param termQuery 查询字段过滤
     * @return 时间与数量的直方图
     */
    public HashMap<String, Integer> dateHistogram(String indexName, String type, String timeField, String startTime, String endTime, Map<String, Object> termQuery) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 设置查询时间范围
        boolQueryBuilder.must(QueryBuilders.rangeQuery(timeField).gte(startTime).lte(endTime));
        // 设置过滤字段
        if (termQuery != null) {
            Set<String> keys = termQuery.keySet();
            for (String key : keys) {
                boolQueryBuilder.must(QueryBuilders.termQuery(key, termQuery.get(key)));
            }
        }

        DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram("group_date").field(timeField).dateHistogramInterval(DateHistogramInterval.DAY).minDocCount(0).format("yyyy-MM-dd");

        builder.query(boolQueryBuilder);
        builder.aggregation(dateHistogramAggregationBuilder);

        searchRequest.source(builder);

        HashMap<String, Integer> map = new HashMap<>();
        // 获取查询结果
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            // 判断查询结果是否超时
            boolean timedOut = searchResponse.isTimedOut();
            if (timedOut) {
                log.error("## Search result from [{}] is timeout!", indexName);
            }
            // 获取查询结果
            if (searchResponse.status().getStatus() == 200) {
                Aggregations aggregations = searchResponse.getAggregations();
                Aggregation group_date = aggregations.get("group_date");

                String jsonString = JSONObject.toJSONString(group_date);
                JSONObject jsonObject = JSONObject.parseObject(jsonString);

                JSONArray buckets = jsonObject.getJSONArray("buckets");
                // 遍历结果集，封装
                for (int i = 0; i < buckets.size(); i++) {
                    JSONObject bucket = buckets.getJSONObject(i);
//                    JSONObject jo = bucket.fluentRemove("aggregations").fluentRemove("fragment").fluentRemove("key");
                    String keyAsString = bucket.getString("keyAsString");
                    Integer docCount = bucket.getInteger("docCount");
                    map.put(keyAsString, docCount);
                }

            }
        } catch (Exception e) {
            log.error("## Get search results occur error: {}, error info: {}", e.getMessage(), e);
        }
        return map;
    }

    /**
     * 根据指定条件，指定字段求和
     * @param indexName
     * @param type
     * @param sumField 求和字段
     * @param terms 查询条件
     * @return
     */
    public HashMap<String, Long> getFieldSum(String indexName, String type, String[] sumField, HashMap<String, Object> terms) {
        HashMap<String, Long> result = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 设置过滤字段
        Iterator<Map.Entry<String, Object>> it = terms.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> next = it.next();
            String key = next.getKey();
            Object value = next.getValue();
            boolQueryBuilder.must(QueryBuilders.termQuery(key, value));
        }

        for (int i = 0; i < sumField.length; i++) {
            SumAggregationBuilder sum = AggregationBuilders.sum(sumField[i]).field(sumField[i]);
            builder.aggregation(sum);
        }

        builder.size(0);
        builder.query(boolQueryBuilder);
        searchRequest.source(builder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            // 判断查询结果是否超时
            boolean timedOut = searchResponse.isTimedOut();
            if (timedOut) {
                log.error("## Search result from [{}] is timeout!", indexName);
            }
            // 获取查询结果
            if (searchResponse.status().getStatus() == 200) {
                Aggregations aggregations = searchResponse.getAggregations();

                for (int j =0; j < sumField.length; j++) {
                    Aggregation agg = aggregations.get(sumField[j]);
                    String jsonString = JSONObject.toJSONString(agg);
                    JSONObject jsonObject = JSONObject.parseObject(jsonString);
                    Long value = jsonObject.getLongValue("value");
                    result.put(sumField[j], value);
                }
            }
        } catch (Exception e) {
            log.error("## Get search results occur error: {}, error info: {}", e.getMessage(), e);
        }

        return result;
    }


}
