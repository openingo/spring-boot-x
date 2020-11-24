package org.openingo.spring.extension.data.elasticsearch.config;

import org.elasticsearch.client.Client;
import org.openingo.spring.extension.data.elasticsearch.ElasticsearchTemplateLite;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;

/**
 * ElasticsearchConfig
 *
 * @author Qicz
 */
@Configuration
@ConditionalOnClass({Client.class, ElasticsearchTemplate.class, ElasticsearchOperations.class})
@AutoConfigureAfter(ElasticsearchAutoConfiguration.class)
public class ElasticsearchConfig {

    /**
     * @return the default elasticsearch template lite
     */
    @Bean
    @ConditionalOnMissingBean(name = "elasticsearchTemplateLite")
    public ElasticsearchTemplateLite elasticsearchTemplateLite(Client elasticsearchClient, ElasticsearchConverter converter) {
        try {
            return new ElasticsearchTemplateLite(elasticsearchClient, converter);
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}