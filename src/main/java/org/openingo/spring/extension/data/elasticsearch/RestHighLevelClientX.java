package org.openingo.spring.extension.data.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.openingo.java.lang.ThreadLocalX;
import org.openingo.jdkits.collection.ListKit;
import org.openingo.jdkits.json.JacksonKit;
import org.openingo.jdkits.reflect.ClassKit;
import org.openingo.jdkits.validate.ValidateKit;
import org.springframework.data.domain.Page;
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

    public RestHighLevelClient restClient() {
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

    private String getHolder() {
        String holder = PROCESSING_WAY_HOLDER.getRemove();
        if (ValidateKit.isNull(holder)) {
            holder = SYNC;
        }
        return holder;
    }

    public boolean putMapDoc(String index, String docId, Map<String, Object> map) throws IOException {
        return this.putMapDoc(index, docId, map, WriteRequest.RefreshPolicy.NONE);
    }

    public boolean putMapDoc(String index, String docId, Map<String, Object> map, WriteRequest.RefreshPolicy refreshPolicy) throws IOException {
        Assert.notNull(map, "the map can not be null");
        String json = JacksonKit.toJson(map);
        return this.putDoc(index, docId, json, XContentType.JSON, refreshPolicy);
    }

    public boolean putJsonDoc(String index, String docId, String json) throws IOException {
        return this.putDoc(index, docId, json, XContentType.JSON, WriteRequest.RefreshPolicy.NONE);
    }

    public boolean putJsonDoc(String index, String docId, String json, WriteRequest.RefreshPolicy refreshPolicy) throws IOException {
        return this.putDoc(index, docId, json, XContentType.JSON, refreshPolicy);
    }

    public boolean putDoc(String index, String docId, String source, XContentType xContentType) throws IOException {
        return this.putDoc(index, docId, source, xContentType, WriteRequest.RefreshPolicy.NONE);
    }

    public boolean putDoc(String index, String docId, String source, XContentType xContentType, WriteRequest.RefreshPolicy refreshPolicy) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index).id(docId).source(source, xContentType);
        indexRequest.setRefreshPolicy(refreshPolicy);
        return this.putDoc(indexRequest);
    }

    public boolean putDoc(IndexRequest indexRequest) throws IOException {
        return this.putDoc(indexRequest, RequestOptions.DEFAULT);
    }

    public boolean putDoc(IndexRequest indexRequest, RequestOptions options) throws IOException {
        String holder = this.getHolder();
        final boolean[] ret = {true};
        switch (holder) {
            case SYNC: {
                IndexResponse response = this.restHighLevelClient.index(indexRequest, options);
                ret[0] = ValidateKit.isNotNull(response) && RestStatus.CREATED.equals(response.status());
            }
            break;
            case ASYNC: {
                this.restHighLevelClient.indexAsync(indexRequest, options, new ActionListener<IndexResponse>() {
                    @Override
                    public void onResponse(IndexResponse indexResponse) {
                        ret[0] = ValidateKit.isNotNull(indexResponse) && RestStatus.CREATED.equals(indexResponse.status());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ret[0] = false;
                    }
                });
            }
            break;
        }
        return ret[0];
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
        String holder = this.getHolder();
        BulkRequest bulkRequest = new BulkRequest();
        docIds.forEach(docId -> {
            DeleteRequest deleteRequest = new DeleteRequest(index, docId);
            bulkRequest.add(deleteRequest);
        });
        bulkRequest.setRefreshPolicy(refreshPolicy);
        final boolean[] ret = {true};
        switch (holder) {
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
                        ret[0] = false;
                    }
                });
            }
                break;
        }
        return ret[0];
    }

    private GetResponse findOneAsResponse(String index, String docId) throws IOException {
        GetRequest getRequest = new GetRequest(index).id(docId);
        String holder = this.getHolder();
        final GetResponse[] documentFields = {null};
        switch (holder) {
            case SYNC: {
                documentFields[0] = this.restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);

            }
            break;
            case ASYNC: {
                this.restHighLevelClient.getAsync(getRequest, RequestOptions.DEFAULT, new ActionListener<GetResponse>() {
                    @Override
                    public void onResponse(GetResponse asyncDocumentFields) {
                        documentFields[0] = asyncDocumentFields;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        log.info("async get failure => \"{}\"", e.getLocalizedMessage());
                    }
                });
            }
        }
        return documentFields[0];
    }

    public Map<String, Object> findOneAsMapById(String index, String docId) throws IOException {
        GetResponse documentFields = this.findOneAsResponse(index, docId);
        if (ValidateKit.isNull(documentFields)) {
            return new HashMap<>();
        }
        return documentFields.getSourceAsMap();
    }

    public <T> T findOneById(Class<T> clazz, String index, String docId) throws IOException {
        GetResponse documentFields = this.findOneAsResponse(index, docId);
        if (ValidateKit.isNull(documentFields)) {
            return ClassKit.newInstance(clazz);
        }
        // json string
        String sourceAsString = documentFields.getSourceAsString();
        return JacksonKit.toObj(sourceAsString, clazz);
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
    public <T> Page<T> searchForPage(Class<T> clazz, String index, SearchSourceBuilder searchSourceBuilder, int pageNumber, int pageSize) throws IOException {
        Assert.notNull(searchSourceBuilder, "the searchSourceBuilder cannot be null");
        if (pageNumber <= 0) {
            pageNumber = 0;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }
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
        log.debug("pagination = \"{}\",  response => \"{}\"", aggregatedPage, response);
        return aggregatedPage;
    }

    /**
     * highlight the page data
     * @param responseHits resp hits
     * @throws JsonProcessingException
     */
    private <T> boolean highlightPageData(Class<T> clazz, boolean highlight, List<T> searchRet, SearchHits responseHits) throws JsonProcessingException {
        SearchHit[] hits = responseHits.getHits();
        if (ValidateKit.isEmpty(hits)) {
            return false;
        }
        List<Map<String, Object>> tmpRet = ListKit.emptyArrayList();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (highlight) {
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
            }
            tmpRet.add(sourceAsMap);
        }
        searchRet.addAll(JacksonKit.toList(JacksonKit.toJson(tmpRet), clazz));
        return true;
    }
}
