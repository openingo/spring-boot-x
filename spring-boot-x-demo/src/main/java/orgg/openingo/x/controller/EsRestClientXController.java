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

package orgg.openingo.x.controller;

import lombok.SneakyThrows;
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
import org.openingo.spring.extension.data.elasticsearch.builder.index.MappingsProperties;
import org.openingo.spring.extension.data.elasticsearch.builder.index.MappingsProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * EsController
 *
 * @author zhucongqi
 */
@RestController
@RequestMapping("/esx")
public class EsRestClientXController {

    @Autowired
    RestHighLevelClientX restHighLevelClientX;

    @SneakyThrows
    @GetMapping("/recommend")
    public List<Map> recommends(String q) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        // 如果该属性中有多个关键字 则都高亮
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.field("name");
        highlightBuilder.field("addr");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        q = "*" + q + "*";
        boolQueryBuilder.must(QueryBuilders.wildcardQuery("name", q));
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("addr", q));
        return this.restHighLevelClientX.randomRecommend(Map.class, "qicz", 3, boolQueryBuilder, highlightBuilder);
    }

    @GetMapping("/add")
    public String addIndex() {
        try {
            MappingsProperties mappingsProperties = MappingsProperties.me();
            mappingsProperties.add(MappingsProperty.me().name("id").type("long"));
            mappingsProperties.add(MappingsProperty.me().name("name").textType().analyzer("ik_smart"));
            restHighLevelClientX.createIndex("aaabc", null, mappingsProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @PostMapping("/put")
    public String putDoc(@RequestBody Map user) {
        boolean ret = false;
        IndexRequest indexRequest = new IndexRequest("qicz");
        indexRequest.id(user.get("id").toString());
        try {
            indexRequest.source(JacksonKit.toJson(user), XContentType.JSON);
            ret = this.restHighLevelClientX.saveOrUpdate(indexRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret ? indexRequest.id() : null;
    }

    @GetMapping("/user/{id}")
    public Map findOneById(@PathVariable("id") Integer id) {
        Map<String, Object> ret = null;
        try {
            ret = this.restHighLevelClientX.findAsMapById("qicz", id.toString());
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
            response = this.restHighLevelClientX.client().delete(deleteRequest, RequestOptions.DEFAULT);
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
