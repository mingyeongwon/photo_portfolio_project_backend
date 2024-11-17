package com.example.portfolio.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;


@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
//        // Create a more permissive type validator
//        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
//            .allowIfBaseType(Object.class)  // Allow all types but still require explicit typing
//            .build();
//
//        // Configure ObjectMapper with necessary modules and settings
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapperUtility.addCustomSliceImplToObjectMapper(objectMapper);
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.registerModule(new Jdk8Module());  // For Optional and other JDK 8 types
//        objectMapper.registerModule(new Pageable);  // Custom module for Spring Data types
//
//        // Configure type handling
//        objectMapper.activateDefaultTyping(
//            typeValidator,
//            ObjectMapper.DefaultTyping.NON_FINAL
//            JsonTypeInfo.As.PROPERTY  // Use property instead of wrapper array
//        );
//
//        // Disable timestamps for dates and handle empty beans
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
//        
//        // Create serializer with configured ObjectMapper
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
//
//        // Configure Redis cache
    	RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//            .entryTtl(Duration.ofHours(24))  // Set default TTL
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer)
            );
//
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(cacheConfiguration)
            .build();
    }
}

