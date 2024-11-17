package com.example.portfolio.config;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ObjectMapperUtility {
	
	public static void addCustomSliceImplToObjectMapper(ObjectMapper objectMapper) {
		SimpleModule module = new SimpleModule();
		module.addAbstractTypeMapping(Slice.class, SliceImpl.class);
		objectMapper.registerModule(module);
	}

}
