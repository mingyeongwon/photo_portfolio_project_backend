package com.example.portfolio.mapper;

import com.example.portfolio.dto.CategoryCreateDto;
import com.example.portfolio.dto.CategoryDto;
import com.example.portfolio.dto.CategoryUpdateDto;
import com.example.portfolio.dto.SubCategoryDto;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.SubCategory;

public class CategoryMapper {

	// Category -> CategoryDto 변환
	public static CategoryDto toDto(Category category) {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId(category.getId());
		categoryDto.setName(category.getName());
		categoryDto
				.setSubCategories(category.getSubCategories().stream().map(CategoryMapper::toSubCategoryDto).toList());
		return categoryDto;
	}

	// CategoryCreateDto -> Category 변환
	public static Category toEntity(CategoryCreateDto categoryCreateDto) {
		Category category = new Category();
		category.setName(categoryCreateDto.getName());
		return category;
	}

	// CategoryUpdateDto -> Category 변환 (업데이트)
	public static void updateEntity(CategoryUpdateDto categoryUpdateDto, Category category) {
		if (categoryUpdateDto.getName() != null) {
			category.setName(categoryUpdateDto.getName());
		}
	}

	// SubCategory -> SubCategoryDto 변환
	public static SubCategoryDto toSubCategoryDto(SubCategory subCategory) {
		SubCategoryDto subCategoryDto = new SubCategoryDto();
		subCategoryDto.setId(subCategory.getId());
		subCategoryDto.setName(subCategory.getName());
		return subCategoryDto;
	}
}
