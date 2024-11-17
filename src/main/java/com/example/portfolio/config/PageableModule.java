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

        addDeserializer(SliceImpl.class, new JsonDeserializer<SliceImpl>() {
            @Override
            public SliceImpl deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                ObjectMapper mapper = (ObjectMapper) p.getCodec();
                JsonNode node = mapper.readTree(p);

                try {
                    // Extract content
                    List<?> content = mapper.convertValue(
                        node.has("content") ? node.get("content") : mapper.createArrayNode(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Object.class)
                    );

                    // Extract pageable information
                    int number = node.has("number") ? node.get("number").asInt() : 0;
                    int size = node.has("size") ? node.get("size").asInt() : 10;
                    boolean hasNext = node.has("hasNext") && node.get("hasNext").asBoolean();

                    // Create pageable
                    Pageable pageable = PageRequest.of(number, size);
                    return new SliceImpl<>(content, pageable, hasNext);

                } catch (Exception e) {
                    // Add detailed error logging
                    throw e;
                }
            }
        });
    }
}
