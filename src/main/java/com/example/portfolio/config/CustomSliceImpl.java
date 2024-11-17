package com.example.portfolio.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class CustomSliceImpl<T> extends SliceImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomSliceImpl(
            @JsonProperty("content") List<T> content,
            @JsonProperty("pageable") Pageable pageable,
            @JsonProperty("first") boolean first,
            @JsonProperty("last") boolean last,
            @JsonProperty("size") int size,
            @JsonProperty("number") int number,
            @JsonProperty("sort") Sort sort,
            @JsonProperty("numberOfElements") int numberOfElements,
            @JsonProperty("empty") boolean empty
    ) {
        super(content, PageRequest.of(number, size, sort), !last);
    }

    public CustomSliceImpl(List<T> content, Pageable pageable, boolean hasNext) {
        super(content, pageable, hasNext);
    }

    public CustomSliceImpl() {
        super(new ArrayList<>());
    }
}
       
