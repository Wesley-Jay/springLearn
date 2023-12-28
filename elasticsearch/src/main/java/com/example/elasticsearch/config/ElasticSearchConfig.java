package com.example.elasticsearch.config;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wsj
 * @description ES配置
 * @date 2023/11/9
 */

@Configuration
public class ElasticSearchConfig {

    @Value("${spring.es.uris}")
    private String hosts;
    @Value("${spring.es.username}")
    private String name;
    @Value("${spring.es.password}")
    private String password;

    @Bean
    public ElasticsearchClient docqaElasticsearchClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(name, password));
        List<HttpHost> httpHosts = new ArrayList<>();
        String[] split = hosts.split(",");
        for (String s : split) {
            httpHosts.add(HttpHost.create(s));
        }
        HttpHost[] httpHosts1 = httpHosts.toArray(new HttpHost[0]);
        RestClient client = RestClient
                .builder(httpHosts1)
                .setHttpClientConfigCallback(httpAsyncClientBuilder ->
                        httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider).setKeepAliveStrategy((response, context) -> 180 * 1000))
                .build();

        ElasticsearchTransport transport = new RestClientTransport(client, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
