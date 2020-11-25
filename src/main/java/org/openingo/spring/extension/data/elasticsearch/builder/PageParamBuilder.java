package org.openingo.spring.extension.data.elasticsearch.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.extension.data.elasticsearch.kit.KeywordKit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

/**
 * PageParamBuilder
 *
 * @author Qicz
 */
public final class PageParamBuilder<T> {

    private PageParamBuilder() {}

    private Class<T> clazz;
    private String keyword;
    private String highlightColor;
    private Pageable pageable;
    private HighlightBuilder.Field[] highlightFields;
    private QueryBuilder queryBuilder;
    private QueryBuilder filterBuilder;
    private SortBuilder<?> sortBuilder;

    public static <T> PageParamBuilder<T> builder() {
        return new PageParamBuilder<>();
    }

    public PageParamBuilder<T> clazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    public PageParamBuilder<T> keyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public PageParamBuilder<T> keywords(String... keywords) {
        this.keyword = KeywordKit.toKeyword(keywords);
        return this;
    }

    public PageParamBuilder<T> highlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
        return this;
    }

    public PageParamBuilder<T> pageable(Integer pageNum, Integer pageSize) {
        if (pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }
        this.pageable = PageRequest.of(pageNum - 1, pageSize);
        return this;
    }

    public PageParamBuilder<T> highlightFields(HighlightBuilder.Field... highlightFields) {
        this.highlightFields = highlightFields;
        return this;
    }

    public PageParamBuilder<T> query(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        return this;
    }

    public PageParamBuilder<T> matchAllQuery() {
        this.queryBuilder = QueryBuilders.matchAllQuery();
        return this;
    }

    public PageParamBuilder<T> filter(QueryBuilder filterBuilder) {
        this.filterBuilder = filterBuilder;
        return this;
    }

    public PageParamBuilder<T> sort(String fieldSort, SortOrder order, String unmappedType) {
        Assert.hasText(fieldSort, "the sort field is not exist");
        Assert.notNull(order, "the sort order is null");
        FieldSortBuilder sortBuilder = SortBuilders.fieldSort(fieldSort).order(order);
        if (ValidateKit.isNotEmpty(unmappedType)) {
            sortBuilder.unmappedType(unmappedType);
        }
        this.sortBuilder = sortBuilder;
        return this;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getHighlightColor() {
        return highlightColor;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public HighlightBuilder.Field[] getHighlightFields() {
        return highlightFields;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public QueryBuilder getFilterBuilder() {
        return filterBuilder;
    }

    public SortBuilder<?> getSortBuilder() {
        return sortBuilder;
    }
}
