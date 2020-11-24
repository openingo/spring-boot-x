package org.openingo.x.controller;

import org.elasticsearch.search.sort.SortOrder;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.extension.data.elasticsearch.ElasticsearchTemplateLite;
import org.openingo.spring.extension.data.elasticsearch.builder.PageParamBuilder;
import org.openingo.spring.extension.data.elasticsearch.kit.KeywordKit;
import org.openingo.x.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * EsController
 *
 * @author zhucongqi
 */
@RestController
@RequestMapping("/es")
public class EsController {

    @Autowired
    ElasticsearchTemplateLite elasticsearchTemplateLite;

    @PostMapping("/put")
    public String putDoc(@RequestBody User user) {
        return elasticsearchTemplateLite.put(user);
    }

    @GetMapping("/user/{id}")
    public User findOneById(@PathVariable("id") Integer id) {
        return this.elasticsearchTemplateLite.findById(id, User.class);
    }

    @GetMapping("/user")
    public User findOneByUser(@RequestBody User user) {
        return this.elasticsearchTemplateLite.findById(user);
    }

    @GetMapping("/list")
    public Page<?> findForPage(int pageNum, int pageSize, String keywords) {
        PageParamBuilder<User> pageParamBuilder = PageParamBuilder.<User>builder().clazz(User.class).pageable(pageNum, pageSize);
        if (ValidateKit.isNotNull(keywords)) {
//            String keyword = KeywordKit.toKeyword(keywords);
//            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("name", keyword))
//                    .should(QueryBuilders.queryStringQuery(keyword))
//                    ;
//            pageParamBuilder.query(queryBuilder)


                   pageParamBuilder.keyword(keywords)
//                    .highlightFields(new HighlightBuilder.Field("addr"))
                           .highlightColor("red");
        }
        return this.elasticsearchTemplateLite.findForPage(pageParamBuilder);
    }

    @GetMapping("/list_sort")
    public Page<?> findForPageByIdSort(int pageNum, int pageSize, String... keywords) {
        PageParamBuilder<User> pageParamBuilder = PageParamBuilder.<User>builder().clazz(User.class).pageable(pageNum, pageSize).sort("id", SortOrder.DESC, "long");
        if (ValidateKit.isNotNull(keywords)) {
            String keyword = KeywordKit.toKeyword(keywords);
            pageParamBuilder.keyword(keyword).highlightColor("green");
        }
        return this.elasticsearchTemplateLite.findForPage(pageParamBuilder);
    }
}
