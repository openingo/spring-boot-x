package org.openingo.spring.extension.data.elasticsearch;

import lombok.SneakyThrows;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.openingo.jdkits.json.JacksonKit;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.extension.data.elasticsearch.builder.PageParamBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ElasticsearchTemplateLite
 *
 * @author Qicz
 */
public class ElasticsearchTemplateLite extends ElasticsearchTemplate {

    public ElasticsearchTemplateLite(Client client, ElasticsearchConverter elasticsearchConverter) {
        super(client, elasticsearchConverter);
    }

    private String getPersistentEntityId(Object entity) {
        Assert.notNull(entity, "the document can not be null");
        ElasticsearchPersistentEntity<?> persistentEntity = getPersistentEntityFor(entity.getClass());
        Object identifier = persistentEntity.getIdentifierAccessor(entity).getIdentifier();
        Assert.notNull(identifier, "the document id can not be null");
        return identifier.toString();
    }

    /**
     * create or update a doc
     * @param entity doc content
     * @return document id
     */
    public String put(Object entity) {
        IndexQuery indexQuery = new IndexQueryBuilder().withId(this.getPersistentEntityId(entity)).withObject(entity).build();
        return this.index(indexQuery);
    }

    /**
     * create or update a doc
     * @param entity doc content
     * @param parentId parent doc id
     * @return document id
     */
    public String put(Object entity, String parentId) {
        Assert.hasText(parentId, "the parentId is null.");
        IndexQuery indexQuery = new IndexQueryBuilder().withId(this.getPersistentEntityId(entity)).withParentId(parentId).withObject(entity).build();
        return this.index(indexQuery);
    }

    /**
     * delete a doc by id
     * @param entity doc content
     * @param <T>
     * @return document id
     */
    public <T> String deleteById(Object entity) {
        return this.delete(entity.getClass(), this.getPersistentEntityId(entity));
    }

    /**
     * delete a doc by id
     * @param clazz doc class
     * @param id doc id
     * @param <T>
     * @return document id
     */
    public <T> String deleteById(Class<T> clazz, Object id) {
        Assert.notNull(id, "the document id can not be null");
        return this.delete(clazz, id.toString());
    }

    /**
     * find the doc by id
     * @param id document id
     * @param clazz document class
     * @param <T>
     * @return the found doc
     */
    public <T> T findById(Object id, Class<T> clazz) {
        GetQuery getQuery = new GetQuery();
        getQuery.setId(id.toString());
        return this.queryForObject(getQuery, clazz);
    }

    /**
     * find the doc by id
     * @param entity document has id
     * @param <T>
     * @return the found doc
     */
    public <T> T findById(Object entity) {
        GetQuery getQuery = new GetQuery();
        getQuery.setId(this.getPersistentEntityId(entity));
        return (T) this.queryForObject(getQuery, entity.getClass());
    }

    /**
     * find doc for page
     * @param pageParamBuilder page param builder
     * @param <T>
     * @return doc pageable
     */
    public <T> Page<T> findForPage(PageParamBuilder<T> pageParamBuilder) {
        Assert.notNull(pageParamBuilder, "the pageable builder is null.");
        Class<T> clazz = pageParamBuilder.getClazz();
        Assert.notNull(clazz, "the pageable builder clazz property is null.");
        QueryBuilder queryBuilder = pageParamBuilder.getQueryBuilder();
        NativeSearchQueryBuilder searchQueryBuilder = null;
        String keyword = pageParamBuilder.getKeyword();
        if (ValidateKit.isNotEmpty(keyword)) {
            searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(QueryBuilders.queryStringQuery(keyword));
        } else if (ValidateKit.isNotNull(queryBuilder)){
            searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(queryBuilder);
        } else {
            searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery());
        }
        // pageable
        Pageable pageable = pageParamBuilder.getPageable();
        if (ValidateKit.isNotNull(pageable)) {
            searchQueryBuilder.withPageable(pageable);
        }
        // filter
        QueryBuilder filterBuilder = pageParamBuilder.getFilterBuilder();
        if (ValidateKit.isNotNull(filterBuilder)) {
            searchQueryBuilder.withFilter(filterBuilder);
        }
        // sort
        SortBuilder<?> sortBuilder = pageParamBuilder.getSortBuilder();
        if (ValidateKit.isNotNull(sortBuilder)) {
            searchQueryBuilder.withSort(sortBuilder);
        }
        // highlight
        String highlightColor = pageParamBuilder.getHighlightColor();
        if (ValidateKit.isNull(highlightColor)) {
            return this.queryForPage(searchQueryBuilder.build(), clazz);
        }
        HighlightBuilder.Field[] highlightFields = pageParamBuilder.getHighlightFields();
        if (ValidateKit.isNull(highlightFields)) {
            String preTags = "<font color='" + highlightColor + "'>";
            String postTags = "</font>";
            HighlightBuilder.Field highlightField = new HighlightBuilder.Field("*").preTags(preTags).postTags(postTags);
            searchQueryBuilder.withHighlightFields(highlightField);
        } else {
            searchQueryBuilder.withHighlightFields(highlightFields);
        }
        return this.queryForPage(searchQueryBuilder.build(), clazz, new HighlightResultMapper());
    }

    public static class HighlightResultMapper implements SearchResultMapper {
        @SneakyThrows
        @Override
        public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> clazz, Pageable pageable) {
            long totalHits = searchResponse.getHits().getTotalHits();
            List<T> list = new ArrayList<>();
            SearchHits hits = searchResponse.getHits();
            if (hits.getHits().length > 0) {
                for (SearchHit searchHit : hits) {
                    Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                    String sourceAsString = searchHit.getSourceAsString();
                    T item = JacksonKit.toObj(sourceAsString, clazz);
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (highlightFields.containsKey(field.getName())) {
                            try {
                                field.set(item, highlightFields.get(field.getName()).fragments()[0].toString());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    list.add(item);
                }
            }
            return new AggregatedPageImpl<>(list, pageable, totalHits);
        }
    }
}
