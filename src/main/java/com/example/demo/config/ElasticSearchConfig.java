package com.example.demo.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9300}")
    private int port;

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    @Bean
    public RestHighLevelClient restClient() {

        RestClientBuilder builder = RestClient.builder(new HttpHost(this.host, this.port));
        RestHighLevelClient client = new RestHighLevelClient(builder);

        return client;
    }
}
