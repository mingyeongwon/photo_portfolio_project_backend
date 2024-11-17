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
            public SliceImpl<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                ObjectMapper mapper = (ObjectMapper) p.getCodec();
                JsonNode node = mapper.readTree(p);

                try {
                    // 타입 정보가 wrapper array로 올 경우를 처리
                    JsonNode contentNode = node;
                    if (node.isArray() && node.size() > 0) {
                        contentNode = node.get(node.size() - 1);
                    }

                    List<?> content;
                    if (contentNode.has("content")) {
                        JsonNode contentArray = contentNode.get("content");
                        content = mapper.convertValue(
                            contentArray,
                            mapper.getTypeFactory().constructCollectionType(List.class, Object.class)
                        );
                    } else {
                        throw new IOException("Content node not found in JSON");
                    }

                    int number = contentNode.has("number") ? contentNode.get("number").asInt() : 0;
                    int size = contentNode.has("size") ? contentNode.get("size").asInt() : content.size();
                    boolean hasNext = contentNode.has("hasNext") ? contentNode.get("hasNext").asBoolean() : false;

                    Pageable pageable = PageRequest.of(number, size);
                    return new SliceImpl<>(content, pageable, hasNext);
                } catch (Exception e) {
                    throw new IOException("Failed to deserialize SliceImpl: " + e.getMessage() + "\nJSON content: " + node.toString(), e);
                }
            }
        });
    }
}