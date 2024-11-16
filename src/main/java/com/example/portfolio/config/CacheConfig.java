package com.example.portfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class CacheConfig {
	
	@Bean
	public RedisCacheManager redisCacheManager (RedisConnectionFactory redisConnectionFactory) {
	    PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator
	    		.builder()
	    		.allowIfSubType("com.example.portfolio.dto")
	    		.allowIfSubType("java.util")  
	    		.build();
	    
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule()); // Optional: Java Time 모듈
	    objectMapper.activateDefaultTyping(typeValidator,ObjectMapper.DefaultTyping.NON_FINAL);
	    
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
		
		RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
	
		return RedisCacheManager.builder(redisConnectionFactory)
				.cacheDefaults(cacheConfiguration)
				.build();
	}
}
