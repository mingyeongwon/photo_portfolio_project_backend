package com.example.portfolio.config;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class PageableModule extends SimpleModule {
    public PageableModule() {
        super("PageableModule");

        // Add deserializer for SliceImpl
        addDeserializer(SliceImpl.class, new JsonDeserializer<SliceImpl>() {
            @Override
            public SliceImpl deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                ObjectMapper mapper = (ObjectMapper) p.getCodec();
                JsonNode node = mapper.readTree(p);

                // Extract content
                List<?> content = mapper.convertValue(node.get("content"),
                    mapper.getTypeFactory().constructCollectionType(List.class, Object.class));

                // Extract pageable information
                int number = node.get("number").asInt();
                int size = node.get("size").asInt();
                boolean hasNext = node.get("hasNext").asBoolean();

                // Create pageable
                Pageable pageable = PageRequest.of(number, size);

                return new SliceImpl<>(content, pageable, hasNext);
            }
        });
    }
}
