package com.interviewai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig {

    /**
     * Removes the "_class" field from stored MongoDB documents for cleaner data.
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory factory,
            MongoMappingContext context,
            MongoCustomConversions conversions) {
        DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);
        converter.setCustomConversions(conversions);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }
}