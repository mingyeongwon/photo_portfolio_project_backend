package com.example.portfolio.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.portfolio.dto.CategoryCreateDto;
import com.example.portfolio.dto.CategoryUpdateDto;
import com.example.portfolio.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
	CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

	// Entity -> Create Dto
	CategoryCreateDto categoryToCreateDto(Category category);

	// Entity -> Update Dto
	CategoryUpdateDto categoryToUpdateDto(Category category);

	// Update Dto -> Entity
	Category createDtoToEntity(CategoryCreateDto categoryCreateDto);

	// Create Dto -> Entity
	Category updateDtoToEntity(CategoryUpdateDto categoryUpdateDto);
}
