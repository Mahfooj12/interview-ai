package com.interviewai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class HttpClientConfig {

    @Value("${app.ai.deepseek.base-url}")
    private String deepSeekBaseUrl;

    @Value("${app.ai.deepseek.api-key}")
    private String deepSeekApiKey;

    @Value("${app.ai.deepseek.timeout-seconds:90}")
    private long timeoutSeconds;

    @Bean
    public RestClient deepSeekRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(timeoutSeconds));

        return RestClient.builder()
                .baseUrl(deepSeekBaseUrl)
                .requestFactory(factory)
                .defaultHeader("Authorization", "Bearer " + deepSeekApiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}