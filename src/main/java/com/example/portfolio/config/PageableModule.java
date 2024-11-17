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
        
        // Add custom deserializer for SliceImpl
        addDeserializer(SliceImpl.class, new JsonDeserializer<SliceImpl>() {
            @Override
            public SliceImpl deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                ObjectMapper mapper = (ObjectMapper) p.getCodec();
                JsonNode node = mapper.readTree(p);
                
                try {
                    // If the node has @class property, get the actual content node
                    JsonNode contentNode = node;
                    if (node.has("@class")) {
                        // The actual content is in the same node, no need to get a child node
                        contentNode = node;
                    }

                    // Extract content array
                    List<?> content = mapper.convertValue(
                        contentNode.get("content"),
                        mapper.getTypeFactory().constructCollectionType(List.class, Object.class)
                    );

                    // Extract pagination information
                    int number = contentNode.get("number").asInt();
                    int size = contentNode.get("size").asInt();
                    boolean hasNext = contentNode.get("hasNext").asBoolean();

                    // Create pageable object
                    Pageable pageable = PageRequest.of(number, size);
                    
                    // Create and return SliceImpl
                    return new SliceImpl<>(content, pageable, hasNext);
                } catch (Exception e) {
                    System.err.println("Error deserializing SliceImpl: " + e.getMessage());
                    System.err.println("JSON content: " + node.toString());
                    throw new RuntimeException("Failed to deserialize SliceImpl", e);
                }
            }
        });
    }
}
