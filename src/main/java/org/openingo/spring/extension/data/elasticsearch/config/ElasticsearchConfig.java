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

/**
 * ElasticsearchConfig
 *
 * @author Qicz
 */
@Configuration
@ConditionalOnClass({Client.class, ElasticsearchTemplate.class, ElasticsearchOperations.class})
@AutoConfigureAfter(ElasticsearchAutoConfiguration.class)
public class ElasticsearchConfig {

    @Bean
    @ConditionalOnMissingBean(name = "elasticsearchTemplateLite")
    public ElasticsearchTemplateLite elasticsearchTemplateLite() {
        return new ElasticsearchTemplateLite();
    }
}
