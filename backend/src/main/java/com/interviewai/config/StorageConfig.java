package com.interviewai.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StorageConfig {

    @Value("${app.storage.cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${app.storage.cloudinary.api-key:}")
    private String apiKey;

    @Value("${app.storage.cloudinary.api-secret:}")
    private String apiSecret;

    @Bean
    @ConditionalOnProperty(name = "app.storage.provider", havingValue = "cloudinary")
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        config.put("secure", "true");
        return new Cloudinary(config);
    }
}
