/*
 * MIT License
 *
 * Copyright (c) 2020 OpeningO Co.,Ltd.
 *
 *    https://openingo.org
 *    contactus(at)openingo.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.openingo.spring.extension.data.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.functionscore.RandomScoreFunctionBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.openingo.java.lang.ThreadLocalX;
import org.openingo.jdkits.collection.ListKit;
import org.openingo.jdkits.json.JacksonKit;
import org.openingo.jdkits.reflect.ClassKit;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.extension.data.elasticsearch.builder.DocBuilder;
import org.openingo.spring.extension.data.elasticsearch.builder.index.MappingsProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RestHighLevelClientX
 *
 * @author Qicz
 */
@Slf4j
public class RestHighLevelClientX {

    private static final String ASYNC = "async";

    private static final String SYNC = "sync";

    private static final ThreadLocalX<String> PROCESSING_WAY_HOLDER = new ThreadLocalX<>();

    private RestHighLevelClient restHighLevelClient;

    public RestHighLevelClientX(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public RestHighLevelClient client() {
        return restHighLevelClient;
    }

    /**
     * using async way
     */
    public RestHighLevelClientX async() {
        PROCESSING_WAY_HOLDER.set(ASYNC);
        return this;
    }

    /**
     * using sync way
     */
    public RestHighLevelClientX sync() {
        PROCESSING_WAY_HOLDER.set(SYNC);
        return this;
    }

    private String getProcessingWay() {
        String way = PROCESSING_WAY_HOLDER.getRemove();
        if (ValidateKit.isNull(way)) {
            way = SYNC;
        }
        return way;
    }

    public boolean createIndex(String index, Settings settings, MappingsProperties mappingsProperties) throws IOException {
        boolean exists = this.restHighLevelClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
        if (exists) {
            log.info("index \"{}\" is exist!", index);
            return true;
        }
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
        if (ValidateKit.isNotNull(settings)) {
            createIndexRequest.settings(settings);
        }
        if (ValidateKit.isNotNull(mappingsProperties)) {
            createIndexRequest.mapping(mappingsProperties.toJson(), XContentType.JSON);
        }
        CreateIndexResponse createIndexResponse = this.restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        return ValidateKit.isNotNull(createIndexResponse) && createIndexResponse.isAcknowledged();
    }

    public boolean saveOrUpdate(String index, String docId, Map<String, Object> map) throws IOException {
        return this.saveOrUpdate(index, docId, map, WriteRequest.RefreshPolicy.NONE);
    }

    public boolean saveOrUpdate(String index, String docId, Map<String, Object> map, WriteRequest.RefreshPolicy refreshPolicy) throws IOException {
        Assert.notNull(map, "the map must not be null!");
        String json = JacksonKit.toJson(map);
        return this.saveOrUpdate(index, docId, json, XContentType.JSON, refreshPolicy);
    }

    public boolean saveOrUpdate(String index, String docId, String json) throws IOException {
        return this.saveOrUpdate(index, docId, json, XContentType.JSON, WriteRequest.RefreshPolicy.NONE);
    }

    public boolean saveOrUpdate(String index, String docId, String json, WriteRequest.RefreshPolicy refreshPolicy) throws IOException {
        return this.saveOrUpdate(index, docId, json, XContentType.JSON, refreshPolicy);
    }

    public boolean saveOrUpdate(String index, String docId, String source, XContentType xContentType) throws IOException {
        return this.saveOrUpdate(index, docId, source, xContentType, WriteRequest.RefreshPolicy.NONE);
    }

    public boolean saveOrUpdate(String index, String docId, String source, XContentType xContentType, WriteRequest.RefreshPolicy refreshPolicy) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index).id(docId).source(source, xContentType);
        return this.saveOrUpdate(indexRequest, refreshPolicy);
    }

    public boolean saveOrUpdate(IndexRequest indexRequest) throws IOException {
        return this.saveOrUpdate(indexRequest, WriteRequest.RefreshPolicy.NONE);
    }

    public boolean saveOrUpdate(IndexRequest indexRequest, WriteRequest.RefreshPolicy refreshPolicy) throws IOException {
        Assert.notNull(indexRequest, "the indexRequest must not be null!");
        return this.saveOrUpdateDocs(Collections.singletonList(indexRequest), refreshPolicy);
    }

    public boolean saveOrUpdateDocs(List<IndexRequest> indexRequests) throws IOException {
        Assert.notNull(indexRequests, "the indexRequests must not be null!");
        return this.saveOrUpdateDocs(indexRequests, WriteRequest.RefreshPolicy.NONE);
    }

    public boolean saveOrUpdateDocs(List<IndexRequest> indexRequests, WriteRequest.RefreshPolicy refreshPolicy) throws IOException {
        return this.saveOrUpdateDocs(indexRequests, refreshPolicy, RequestOptions.DEFAULT);
    }

    public boolean saveOrUpdateDocs(List<IndexRequest> indexRequests, WriteRequest.RefreshPolicy refreshPolicy, RequestOptions options) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        if (ValidateKit.isNotNull(refreshPolicy)) {
            bulkRequest.setRefreshPolicy(refreshPolicy);
        }
        indexRequests.forEach(bulkRequest::add);
        return this.bulkRequest(bulkRequest, options);
    }

    public boolean saveOrUpdateByDocBuilders(List<DocBuilder> builders) throws IOException {
        return this.saveOrUpdateByDocBuilders(builders, WriteRequest.RefreshPolicy.NONE, RequestOptions.DEFAULT);
    }

    public boolean saveOrUpdateByDocBuilders(List<DocBuilder> builders, WriteRequest.RefreshPolicy refreshPolicy) throws IOException {
        return this.saveOrUpdateByDocBuilders(builders, refreshPolicy, RequestOptions.DEFAULT);
    }

    public boolean saveOrUpdateByDocBuilders(List<DocBuilder> builders, WriteRequest.RefreshPolicy refreshPolicy, RequestOptions options) throws IOException {
        Assert.notNull(builders, "the builders must not be null!");
        BulkRequest bulkRequest = new BulkRequest();
        for (DocBuilder docBuilder : builders) {
            IndexRequest indexRequest = new IndexRequest(docBuilder.getDocIndex()).id(docBuilder.getDocId()).source(docBuilder.getDocSource(), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        if (ValidateKit.isNotNull(refreshPolicy)) {
            bulkRequest.setRefreshPolicy(refreshPolicy);
        }
        return this.bulkRequest(bulkRequest, options);
    }

    public boolean deleteByDocId(String index, String docId) throws IOException {
        return this.deleteByDocIds(index, Collections.singletonList(docId), WriteRequest.RefreshPolicy.NONE);
    }

    public boolean deleteByDocId(String index, String docId, WriteRequest.RefreshPolicy refreshPolicy) throws IOException {
        return this.deleteByDocIds(index, Collections.singletonList(docId), refreshPolicy);
    }

    public boolean deleteByDocIds(String index, List<String> docIds) throws IOException {
        return this.deleteByDocIds(index, docIds, WriteRequest.RefreshPolicy.NONE);
    }

    public boolean deleteByDocIds(String index, List<String> docIds, WriteRequest.RefreshPolicy refreshPolicy) throws IOException {
        return this.deleteByDocIds(index, docIds, refreshPolicy, RequestOptions.DEFAULT);
    }

    public boolean deleteByDocIds(String index, List<String> docIds, WriteRequest.RefreshPolicy refreshPolicy, RequestOptions options) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        docIds.forEach(docId -> bulkRequest.add(new DeleteRequest(index, docId)));
        bulkRequest.setRefreshPolicy(refreshPolicy);
        return this.bulkRequest(bulkRequest, options);
    }

    private boolean bulkRequest(BulkRequest bulkRequest, RequestOptions options) throws IOException {
        String way = this.getProcessingWay();
        final boolean[] ret = {true};
        switch (way) {
            case SYNC: {
                BulkResponse bulkResponse = this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                ret[0] = ValidateKit.isNotNull(bulkResponse) && RestStatus.OK.equals(bulkResponse.status());
            }
            break;
            case ASYNC: {
                this.restHighLevelClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
                    @Override
                    public void onResponse(BulkResponse bulkItemResponses) {
                        ret[0] = ValidateKit.isNotNull(bulkItemResponses) && RestStatus.OK.equals(bulkItemResponses.status());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        log.error("the bulk request error \"{}\"", e.getLocalizedMessage());
                        ret[0] = false;
                    }
                });
            }
            break;
            default:
                throw new IllegalStateException("Unexpected value: " + way);
        }
        return ret[0];
    }

    private MultiGetItemResponse[] findAsResponse(String index, List<String> docIds) throws IOException {
        Assert.notNull(docIds, "the docIds must not be null!");
        String way = this.getProcessingWay();
        MultiGetRequest multiGetRequest = new MultiGetRequest();
        docIds.forEach(docId -> multiGetRequest.add(index, docId));
        final MultiGetResponse[] multiGetResponses = new MultiGetResponse[1];
        switch (way) {
            case SYNC: {
                multiGetResponses[0] = this.restHighLevelClient.mget(multiGetRequest, RequestOptions.DEFAULT);
            }
            break;
            case ASYNC: {
                this.restHighLevelClient.mgetAsync(multiGetRequest, RequestOptions.DEFAULT, new ActionListener<MultiGetResponse>() {
                    @Override
                    public void onResponse(MultiGetResponse multiGetItemResponses) {
                        multiGetResponses[0] = multiGetItemResponses;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        log.error("the async mget failure => \"{}\"", e.getLocalizedMessage());
                    }
                });
            }
            break;
            default:
                throw new IllegalStateException("Unexpected value: " + way);
        }
        if (ValidateKit.isNull(multiGetResponses[0])) {
            return null;
        }
        return multiGetResponses[0].getResponses();
    }

    public Map<String, Object> findAsMapById(String index, String docId) throws IOException {
        MultiGetItemResponse[] multiGetItemResponses = this.findAsResponse(index, Collections.singletonList(docId));
        if (ValidateKit.isNull(multiGetItemResponses)) {
            return new HashMap<>();
        }
        return multiGetItemResponses[0].getResponse().getSourceAsMap();
    }

    public <T> T findById(Class<T> clazz, String index, String docId) throws IOException {
        MultiGetItemResponse[] multiGetItemResponses = this.findAsResponse(index, Collections.singletonList(docId));
        if (ValidateKit.isNull(multiGetItemResponses)) {
            return ClassKit.newInstance(clazz);
        }
        // json string
        String sourceAsString = multiGetItemResponses[0].getResponse().getSourceAsString();
        return JacksonKit.toObj(sourceAsString, clazz);
    }

    public List<Map<String, Object>> findAsMapByIds(String index, List<String> docIds) throws IOException {
        MultiGetItemResponse[] multiGetItemResponses = this.findAsResponse(index, docIds);
        if (ValidateKit.isNull(multiGetItemResponses)) {
            return ListKit.emptyArrayList();
        }
        List<Map<String, Object>> docMaps = ListKit.emptyArrayList();
        for (MultiGetItemResponse multiGetItemResponse : multiGetItemResponses) {
            Map<String, Object> sourceAsMap = multiGetItemResponse.getResponse().getSourceAsMap();
            docMaps.add(sourceAsMap);
        }
        return docMaps;
    }

    public <T> List<T> findByIds(Class<T> clazz, String index, List<String> docIds) throws IOException {
        List<Map<String, Object>> docMaps = this.findAsMapByIds(index, docIds);
        if (ValidateKit.isNull(docMaps)) {
            return ListKit.emptyArrayList();
        }
        return JacksonKit.toList(JacksonKit.toJson(docMaps), clazz);
    }

    /**
     * search for page, automatic highlight the result ref the searchSourceBuilder.
     * @param clazz result class
     * @param index es doc index
     * @param searchSourceBuilder search source parameter
     * @param pageNumber page number
     * @param pageSize result size per page
     * @param <T>
     * @return paged result data
     * @throws IOException
     */
    public <T> AggregatedPageImpl<T> searchForPage(Class<T> clazz, String index, SearchSourceBuilder searchSourceBuilder, int pageNumber, int pageSize) throws IOException {
        Assert.notNull(searchSourceBuilder, "the searchSourceBuilder cannot be null");
        // fix the pageable parameters
        if (pageNumber < 0) {
            pageNumber = 0;
        }
        if (pageSize < 0) {
            pageSize = 10;
        }
        // reset searchSourceBuilder from size
        if (searchSourceBuilder.from() == -1) {
            searchSourceBuilder.from(pageNumber * pageSize);
        }
        if (searchSourceBuilder.size() == -1) {
            searchSourceBuilder.size(pageSize);
        }

        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(2L));

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        SearchResponse response = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        AggregatedPageImpl<T> aggregatedPage = this.pagination(clazz, ValidateKit.isNotNull(searchSourceBuilder.highlighter()), response, pageable);
        if (ValidateKit.isNull(aggregatedPage)) {
            aggregatedPage = new AggregatedPageImpl<>(ListKit.emptyList(), pageable, 0);
        }
        return aggregatedPage;
    }

    /**
     * random Recommend some docs
     * @param clazz result class
     * @param index es doc index
     * @param recommendSize recommend doc size, if size <= 0 reset to  8
     * @param <T>
     * @return recommend result
     * @throws IOException
     */
    public <T> List<T> randomRecommend(Class<T> clazz, String index, int recommendSize) throws IOException {
        return randomRecommend(clazz, index, recommendSize, null, null,null, null);
    }

    /**
     * random Recommend some docs
     * @param clazz result class
     * @param index es doc index
     * @param recommendSize recommend doc size, if size <= 0 reset to  8
     * @param highlightBuilder highlight or not
     * @param <T>
     * @return recommend result
     * @throws IOException
     */
    public <T> List<T> randomRecommend(Class<T> clazz,
                                       String index,
                                       int recommendSize,
                                       QueryBuilder queryBuilder,
                                       HighlightBuilder highlightBuilder) throws IOException {
        return randomRecommend(clazz, index, recommendSize, queryBuilder, highlightBuilder, null, null);
    }

    /**
     * random Recommend some docs
     * @param clazz result class
     * @param index es doc index
     * @param recommendSize recommend doc size, if size <= 0 reset to  8
     * @param queryBuilder query data for highlightBuilder
     * @param highlightBuilder highlight or not
     * @param docIdFieldName doc id field name
     * @param excludeDocIds exclude doc ids
     * @param <T>
     * @return recommend result
     * @throws IOException
     */
    public <T> List<T> randomRecommend(Class<T> clazz,
                                       String index,
                                       int recommendSize,
                                       QueryBuilder queryBuilder,
                                       HighlightBuilder highlightBuilder,
                                       String docIdFieldName,
                                       List<String> excludeDocIds) throws IOException {
        if (recommendSize <= 0) {
            recommendSize = 8;
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(recommendSize);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // parser queryBuilder
        if (ValidateKit.isNotNull(queryBuilder)) {
            if (queryBuilder instanceof BoolQueryBuilder) {
                boolQueryBuilder = (BoolQueryBuilder)queryBuilder;
            } else {
                boolQueryBuilder.must(queryBuilder);
            }
        }
        // exclude docs
        if (ValidateKit.isAllNotNull(docIdFieldName, excludeDocIds)) {
            boolQueryBuilder.must(new TermsQueryBuilder(docIdFieldName, excludeDocIds));
        }
        // random the query data, must not use custom order
        boolQueryBuilder.must(QueryBuilders.functionScoreQuery(new RandomScoreFunctionBuilder()));
        searchSourceBuilder.sort(SortBuilders.scoreSort());
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.highlighter(highlightBuilder);

        // do search action
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<T> searchRet = ListKit.emptyArrayList();
        this.highlightPageData(clazz, ValidateKit.isNotNull(highlightBuilder), searchRet, response.getHits());
        return searchRet;
    }

    /**
     * pagination and response data package
     * @param response search response
     * @param pageable pagination parameters
     * @throws IOException
     */
    private <T> AggregatedPageImpl<T> pagination(Class<T> clazz, boolean highlight, SearchResponse response, PageRequest pageable) throws IOException {
        AggregatedPageImpl<T> aggregatedPage = null;
        if (ValidateKit.isNotNull(response) && RestStatus.OK.equals(response.status())) {
            List<T> searchRet = ListKit.emptyArrayList();
            SearchHits responseHits = response.getHits();
            if (this.highlightPageData(clazz, highlight, searchRet, responseHits)) {
                long total = 0;
                TotalHits totalHits = responseHits.getTotalHits();
                if (ValidateKit.isNotNull(totalHits)) {
                    total = totalHits.value;
                }
                aggregatedPage = new AggregatedPageImpl<>(searchRet, pageable, total);
            }
        }
        log.info("the pagination = \"{}\",  response => \"{}\"", aggregatedPage, response);
        return aggregatedPage;
    }

    /**
     * highlight the page data
     * @param highlight highlight or not
     * @param searchRet result
     * @param responseHits resp hits
     * @throws JsonProcessingException
     */
    private <T> boolean highlightPageData(Class<T> clazz, boolean highlight, List<T> searchRet, SearchHits responseHits) throws JsonProcessingException {
        SearchHit[] hits = responseHits.getHits();
        if (ValidateKit.isEmpty(hits)) {
            return false;
        }
        List<Map<String, Object>> tmpRet = ListKit.emptyArrayList();
        if (!highlight) {
            for (SearchHit hit : hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                tmpRet.add(sourceAsMap);
            }
        } else {
            for (SearchHit hit : hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                highlightFields.keySet().forEach(key -> {
                    Object val = sourceAsMap.get(key);
                    if (!(val instanceof String)) {
                        return;// = continue;
                    }
                    StringBuilder newData = new StringBuilder();
                    HighlightField highlightField = highlightFields.get(key);
                    if (ValidateKit.isNotNull(highlightField)) {
                        Text[] fragments = highlightField.getFragments();
                        for (Text fragment : fragments) {
                            newData.append(fragment.toString());
                        }
                    } else {
                        newData.append(val.toString());
                    }
                    sourceAsMap.put(key, newData.toString());
                });
                tmpRet.add(sourceAsMap);
            }
        }
        if (ValidateKit.isNotEmpty(tmpRet)) {
            searchRet.addAll(JacksonKit.toList(JacksonKit.toJson(tmpRet), clazz));
        }
        return true;
    }
}
