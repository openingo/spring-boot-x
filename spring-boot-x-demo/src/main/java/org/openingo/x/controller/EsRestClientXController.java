package org.openingo.x.controller;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.openingo.jdkits.json.JacksonKit;
import org.openingo.spring.extension.data.elasticsearch.RestHighLevelClientX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 * EsController
 *
 * @author zhucongqi
 */
@RestController
@RequestMapping("/es")
public class EsRestClientXController {

    @Autowired
    RestHighLevelClientX restHighLevelClientX;

    @PostMapping("/put")
    public String putDoc(@RequestBody Map user) {
        boolean ret = false;
        IndexRequest indexRequest = new IndexRequest("qicz");
        indexRequest.id("123");
        try {
            indexRequest.source(JacksonKit.toJson(user), XContentType.JSON);
            ret = this.restHighLevelClientX.putDoc(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret ? indexRequest.id() : null;
    }

    @GetMapping("/user/{id}")
    public Map findOneById(@PathVariable("id") Integer id) {
        Map<String, Object> ret = null;
        try {
            ret = this.restHighLevelClientX.findOneAsMapById("qicz", id.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @DeleteMapping("/user/{id}")
    public RestStatus deleteById(@PathVariable("id") Integer id) {
        DeleteRequest deleteRequest = new DeleteRequest("qicz");
        deleteRequest.id(id.toString());
        DeleteResponse response = null;
        try {
            response = this.restHighLevelClientX.restClient().delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        return response.status();
    }

    @GetMapping("/user/find")
    public Page<?> find(String q) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //q = "q+-&&||!(){}[]^\"~*?:\\";
        q = QueryParser.escape(q);
        boolQueryBuilder.should(QueryBuilders.queryStringQuery(q));
        sourceBuilder.query(boolQueryBuilder);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(true); //如果该属性中有多个关键字 则都高亮
        highlightBuilder.field("docType");
        highlightBuilder.field("docTitle");
        highlightBuilder.field("docContent");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        sourceBuilder.from(1);
        sourceBuilder.size(10);
        sourceBuilder.highlighter(highlightBuilder);
        try {
            return this.restHighLevelClientX.searchForPage(Object.class, "qicz", sourceBuilder, 1, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
