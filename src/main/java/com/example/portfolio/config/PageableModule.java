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
                    JsonNode contentNode = node;
                    List<?> content;
                    
                    // content 노드 처리
                    if (contentNode.has("content")) {
                        JsonNode contentArray = contentNode.get("content");
                        content = mapper.convertValue(
                            contentArray,
                            mapper.getTypeFactory().constructCollectionType(List.class, Object.class)
                        );
                    } else {
                        throw new IOException("Content node not found in JSON");
                    }

                    // 페이지 정보 추출
                    int number = contentNode.has("number") ? contentNode.get("number").asInt() : 0;
                    int size = contentNode.has("size") ? contentNode.get("size").asInt() : content.size();
                    //boolean hasNext = contentNode.has("hasNext") ? contentNode.get("hasNext").asBoolean() : false;
                    boolean hasNext = contentNode.get("hasNext").asBoolean();

                    // Pageable 객체 생성
                    Pageable pageable = PageRequest.of(number, size);

                    // SliceImpl 생성 및 반환
                    return new SliceImpl<>(content, pageable, hasNext);
                } catch (Exception e) {
                    throw new IOException("Failed to deserialize SliceImpl: " + e.getMessage(), e);
                }
            }
        });
    }
}