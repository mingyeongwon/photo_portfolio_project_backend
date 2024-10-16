package com.example.portfolio.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.portfolio.dto.CategoryCreateDto;
import com.example.portfolio.dto.CategoryUpdateDto;
import com.example.portfolio.dto.SubCategoryCreateDto;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.SubCategory;

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

	@Mapping(source = "categoryId", target = "category.id")
	SubCategory createSubCategoryToSubCategory(SubCategoryCreateDto subCategoryCreateDto);
	
	@Mapping(source = "category.id", target = "categoryId")
	SubCategoryCreateDto createSubCategoryToSubCategoryDto(SubCategory subCategory);
}
