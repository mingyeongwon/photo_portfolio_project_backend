package com.example.portfolio.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.portfolio.dto.CategoryCreateDto;
import com.example.portfolio.dto.CategoryDto;
import com.example.portfolio.dto.SubCategoryDto;
import com.example.portfolio.mapper.CategoryMapper;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.SubCategory;
import com.example.portfolio.repository.CategoryRepository;
import com.example.portfolio.repository.SubCategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final SubCategoryRepository subCategoryRepository;
	private final CategoryMapper categoryMapper;

	// 생성자
	public CategoryService(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository,
			CategoryMapper categoryMapper) {
		this.categoryRepository = categoryRepository;
		this.subCategoryRepository = subCategoryRepository;
		this.categoryMapper = categoryMapper;
	}

	public List<Category> getCategory() {

		return categoryRepository.findAll();
	}

	// 카테고리 전체 목록 가져오기
	public List<CategoryDto> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		return categories.stream().map(this::mapEntityToDto).toList();
	}

	public List<SubCategoryDto> getSubCategory(Long categoryId) {
		List<SubCategory> subCategories = subCategoryRepository.findByCategory_id(categoryId);
		// this::subCategoryEntityToDto = subCategoryDto ->
		// this.mapSubCategoryDtoToEntity(subCategoryDto)
		return subCategories.stream().map(this::subCategoryEntityToDto).toList();
	}

	@Transactional
	public void createCategories(List<CategoryCreateDto> categoryCreateDtos) {
		for (CategoryCreateDto categoryCreateDto : categoryCreateDtos) {
			// DTO에서 Category로 변환
			Category category = categoryMapper.createDtoToEntity(categoryCreateDto);
			// 카테고리 먼저 저장
			// SubCategory의 category 설정
			for (SubCategory subCategory : category.getSubCategories()) {
				subCategory.setCategory(category);
			}
			categoryRepository.save(category);
		}
	}

	@Transactional
	public void updateCategories(List<CategoryDto> categoryDtos) {
		for (CategoryDto categoryDto : categoryDtos) {
			// 카테고리를 데이터베이스에서 찾음
			Category category = categoryRepository.findById(categoryDto.getId())
					.orElseThrow(() -> new RuntimeException("Category not found"));

			// 이름이 존재하면 업데이트
			if (categoryDto.getName() != null) {
				category.setName(categoryDto.getName());
			}

			// 서브카테고리가 null이 아닐 때만 처리
			if (categoryDto.getSubCategories() != null) {
				for (SubCategoryDto subCategoryDto : categoryDto.getSubCategories()) {
					// 서브카테고리를 데이터베이스에서 찾거나 새로운 서브카테고리를 생성
					SubCategory subCategory = subCategoryRepository.findById(subCategoryDto.getId())
							.orElse(new SubCategory());
					// 서브카테고리의 이름이 있으면 업데이트
					if (subCategoryDto.getName() != null) {
						subCategory.setName(subCategoryDto.getName());
					}
					subCategory.setCategory(category);
					subCategoryRepository.save(subCategory);
				}
			}

			categoryRepository.save(category);
		}
	}

	@Transactional
	public void deleteCategories(List<CategoryDto> categoryDtos) {
		for (CategoryDto categoryDto : categoryDtos) {
			// 카테고리를 데이터베이스에서 찾음
			Category category = categoryRepository.findById(categoryDto.getId())
					.orElseThrow(() -> new RuntimeException("Category not found"));

			// 서브카테고리가 null이 아니면 해당 서브카테고리 삭제
			if (categoryDto.getSubCategories() != null) {
				for (SubCategoryDto subCategoryDto : categoryDto.getSubCategories()) {
					// 서브카테고리를 데이터베이스에서 찾음
					SubCategory subCategory = subCategoryRepository.findById(subCategoryDto.getId())
							.orElseThrow(() -> new RuntimeException("SubCategory not found"));
					// 서브카테고리 삭제
					subCategoryRepository.delete(subCategory);
				}
			}

			// 카테고리 삭제
			categoryRepository.delete(category);
		}
	}

}
