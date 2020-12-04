package org.openingo.spring.extension.data.elasticsearch.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.openingo.spring.extension.data.elasticsearch.RestHighLevelClientX;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ElasticsearchRestHighLevelClientConfig
 *
 * @author Qicz
 */
@Configuration
@ConditionalOnClass(RestHighLevelClient.class)
public class ElasticsearchRestHighLevelClientConfig {

    @Bean
    @ConditionalOnMissingBean(name = "restHighLevelClientX")
    public RestHighLevelClientX restHighLevelClientX(RestHighLevelClient restHighLevelClient) {
        return new RestHighLevelClientX(restHighLevelClient);
    }
}
