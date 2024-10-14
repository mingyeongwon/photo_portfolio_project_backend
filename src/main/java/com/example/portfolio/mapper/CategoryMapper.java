package com.example.portfolio.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.portfolio.dto.CategoryCreateDto;
import com.example.portfolio.dto.CategoryUpdateDto;
import com.example.portfolio.model.Category;

@Mapper
public interface CategoryMapper {
	CategoryMapper ININSTANCE = Mappers.getMapper (CategoryMapper.class);
	//Entity -> Create Dto
	CategoryCreateDto caregoryToCreateDto(Category category);
	//Entity -> Update Dto
	CategoryUpdateDto caregoryToUpdateDto(Category category);
}
