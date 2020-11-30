package org.openingo.x.controller;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.sort.SortOrder;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.extension.data.elasticsearch.ElasticsearchTemplateLite;
import org.openingo.spring.extension.data.elasticsearch.builder.PageParamBuilder;
import org.openingo.spring.extension.data.elasticsearch.kit.KeywordKit;
import org.openingo.x.entity.User;
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
public class EsController {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @PostMapping("/put")
    public String putDoc(@RequestBody Map user) {
        IndexRequest indexRequest = new IndexRequest("qicz", "abc", "123");
        indexRequest.source(user);
        IndexResponse response = null;
        try {
            response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        return response.getId();
    }

//    @PostMapping("/putdoc")
//    public String putUserDoc(@RequestBody User.UserDoc userDoc, String parentId) {
//        return elasticsearchTemplateLite.put(userDoc, parentId);
//    }
//
    @GetMapping("/user/{id}")
    public Map findOneById(@PathVariable("id") Integer id) {
        GetRequest getRequest = new GetRequest("qicz").type("abc").id(id.toString());
        GetResponse documentFields = null;
        try {
            documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert documentFields != null;
        return documentFields.getSource();
    }

    @DeleteMapping("/user/{id}")
    public RestStatus deleteById(@PathVariable("id") Integer id) {
        DeleteRequest deleteRequest = new DeleteRequest("qicz");
        deleteRequest.type("abc");
        deleteRequest.id(id.toString());
        DeleteResponse response = null;
        try {
            response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        return response.status();
    }
//
//    @GetMapping("/user")
//    public User findOneByUser(@RequestBody User user) {
//        return this.elasticsearchTemplateLite.findById(user);
//    }
//
//    @GetMapping("/list")
//    public Page<?> findForPage(int pageNum, int pageSize, String keywords) {
//        PageParamBuilder<User> pageParamBuilder = PageParamBuilder.<User>builder().clazz(User.class).pageable(pageNum, pageSize);
//        if (ValidateKit.isNotNull(keywords)) {
//            String keyword = KeywordKit.toKeyword(keywords);
////            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("name", keyword))
////                    .should(QueryBuilders.queryStringQuery(keyword))
////                    ;
////            pageParamBuilder.query(queryBuilder)
//
//
//             pageParamBuilder.keyword(keywords)
////            HasChildQueryBuilder docContent = JoinQueryBuilders.hasChildQuery("x-user-doc", QueryBuilders.matchQuery("docContent", keyword), ScoreMode.None).innerHit(new InnerHitBuilder());
////            pageParamBuilder.query(docContent)
////                    .highlightFields(new HighlightBuilder.Field("addr"))
//                           .highlightColor("red")
//             ;
//
//        }
//        return this.elasticsearchTemplateLite.findForPage(pageParamBuilder);
//    }
//
//    @GetMapping("/list_sort")
//    public Page<?> findForPageByIdSort(int pageNum, int pageSize, String... keywords) {
//        PageParamBuilder<User> pageParamBuilder = PageParamBuilder.<User>builder().clazz(User.class).pageable(pageNum, pageSize).sort("id", SortOrder.DESC, "long");
//        if (ValidateKit.isNotNull(keywords)) {
//            String keyword = KeywordKit.toKeyword(keywords);
//            pageParamBuilder.keyword(keyword).highlightColor("green");
//        }
//        return this.elasticsearchTemplateLite.findForPage(pageParamBuilder);
//    }

// == template

    @Autowired
    ElasticsearchTemplateLite elasticsearchTemplateLite;

    @PostMapping("/put2")
    public String putDoc(@RequestBody User user) {
        return elasticsearchTemplateLite.put(user);
    }

    @PostMapping("/putdoc2")
    public String putUserDoc(@RequestBody User.UserDoc userDoc, String parentId) {
        return elasticsearchTemplateLite.put(userDoc, parentId);
    }

    @GetMapping("/user2/{id}")
    public User findOneById2(@PathVariable("id") Integer id) {
        return this.elasticsearchTemplateLite.findById(id, User.class);
    }

    @GetMapping("/user2")
    public User findOneByUser(@RequestBody User user) {
        return this.elasticsearchTemplateLite.findById(user);
    }

    @GetMapping("/list2")
    public Page<?> findForPage(int pageNum, int pageSize, String keywords) {
        PageParamBuilder<User> pageParamBuilder = PageParamBuilder.<User>builder().clazz(User.class).pageable(pageNum, pageSize);
        if (ValidateKit.isNotNull(keywords)) {
            String keyword = KeywordKit.toKeyword(keywords);
//            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("name", keyword))
//                    .should(QueryBuilders.queryStringQuery(keyword))
//                    ;
//            pageParamBuilder.query(queryBuilder)


             pageParamBuilder.keyword(keywords)
//            HasChildQueryBuilder docContent = JoinQueryBuilders.hasChildQuery("x-user-doc", QueryBuilders.matchQuery("docContent", keyword), ScoreMode.None).innerHit(new InnerHitBuilder());
//            pageParamBuilder.query(docContent)
//                    .highlightFields(new HighlightBuilder.Field("addr"))
                           .highlightColor("red")
             ;

        }
        return this.elasticsearchTemplateLite.findForPage(pageParamBuilder);
    }

    @GetMapping("/list_sort2")
    public Page<?> findForPageByIdSort(int pageNum, int pageSize, String... keywords) {
        PageParamBuilder<User> pageParamBuilder = PageParamBuilder.<User>builder().clazz(User.class).pageable(pageNum, pageSize).sort("id", SortOrder.DESC, "long");
        if (ValidateKit.isNotNull(keywords)) {
            String keyword = KeywordKit.toKeyword(keywords);
            pageParamBuilder.keyword(keyword).highlightColor("green");
        }
        return this.elasticsearchTemplateLite.findForPage(pageParamBuilder);
    }
}
