package com.course.practicaljava.common;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;


@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchConfig.class);


    @Override
    public RestHighLevelClient elasticsearchClient() {

        //ElasticSearch Configuration
        final ClientConfiguration clientConfiguration = ClientConfiguration
                .builder().connectedTo("localhost:9200").build();

        return RestClients.create(clientConfiguration).rest();
    }



}