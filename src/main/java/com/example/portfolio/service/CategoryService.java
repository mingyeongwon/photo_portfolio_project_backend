package com.example.portfolio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.portfolio.dto.*;
import com.example.portfolio.mapper.CategoryMapper;
import com.example.portfolio.model.*;
import com.example.portfolio.repository.*;
import jakarta.transaction.Transactional;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final SubCategoryRepository subCategoryRepository;
	private final CategoryMapper categoryMapper;
	private final ProjectRepository projectRepository;

	// 생성자
	public CategoryService(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository,
			ProjectRepository projectRepository, CategoryMapper categoryMapper) {
		this.categoryRepository = categoryRepository;
		this.subCategoryRepository = subCategoryRepository;
		this.projectRepository = projectRepository;
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
		List<SubCategory> subCategories = subCategoryRepository.findByCategory_Id(categoryId);
		return subCategories.stream().map(this::subCategoryEntityToDto).toList();
	}

	@Transactional
	public CategoryCreateDto createCategories(CategoryCreateDto categoryCreateDto) {
		Category category = categoryMapper.createDtoToEntity(categoryCreateDto);
		return CategoryMapper.INSTANCE.categoryToCreateDto(categoryRepository.save(category));
	}

	@Transactional
	public void updateCategories(List<CategoryUpdateDto> categoryUpdateDtos) {
		for (CategoryUpdateDto categoryUpdateDto : categoryUpdateDtos) {
			Category category = categoryRepository.findById(categoryUpdateDto.getId())
					.orElseThrow(() -> new RuntimeException("Category not found"));

			if (categoryUpdateDto.getName() != null) {
				category.setName(categoryUpdateDto.getName());
			}

			if (categoryUpdateDto.getSubCategories() != null) {
				for (SubCategoryDto subCategoryDto : categoryUpdateDto.getSubCategories()) {
					SubCategory subCategory = subCategoryRepository.findById(subCategoryDto.getId())
							.orElse(new SubCategory());
					if (subCategoryDto.getName() != null) {
						subCategory.setName(subCategoryDto.getName());
					}
					subCategory.setCategory(category);
					subCategoryRepository.save(subCategory);
				}
			}
		}
	}

	@Transactional
	public void deleteCategory(Long categoryId) {
		if (isCategoryUsed(categoryId)) {
			throw new RuntimeException("Category is in use by a project and cannot be deleted.");
		}
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found"));
		categoryRepository.delete(category);
	}

	public boolean isCategoryUsed(Long categoryId) {
		return projectRepository.existsByCategory_Id(categoryId);
	}

	// DTO -> Entity 변환
	private Category mapDtoToEntity(CategoryDto categoryDto) {
		Category category = new Category();
		category.setId(categoryDto.getId());
		category.setName(categoryDto.getName());

		List<SubCategory> subCategories = categoryDto.getSubCategories().stream().map(subCategoryDto -> {
			SubCategory subCategory = new SubCategory();
			subCategory.setId(subCategoryDto.getId());
			subCategory.setName(subCategoryDto.getName());
			subCategory.setCategory(category);
			return subCategory;
		}).toList();

		category.setSubCategories(subCategories);
		return category;
	}

	// Entity -> DTO 변환
	private CategoryDto mapEntityToDto(Category category) {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId(category.getId());
		categoryDto.setName(category.getName());
		List<SubCategoryDto> subCategories = category.getSubCategories().stream().map(this::subCategoryEntityToDto)
				.toList();
		categoryDto.setSubCategories(subCategories);
		return categoryDto;
	}

	private SubCategoryDto subCategoryEntityToDto(SubCategory subCategory) {
		SubCategoryDto subCategoryDto = new SubCategoryDto();
		subCategoryDto.setId(subCategory.getId());
		subCategoryDto.setName(subCategory.getName());
		return subCategoryDto;
	}

	public SubCategoryCreateDto createSubCategory(Long categoryId, SubCategoryCreateDto subCategoryDto) {
		subCategoryDto.setCategoryId(categoryId);
		SubCategory subCategory = categoryMapper.createSubCategoryToSubCategory(subCategoryDto);
		return CategoryMapper.INSTANCE.createSubCategoryToSubCategoryDto(subCategoryRepository.save(subCategory));
	}

	public void deleteSubCategory(Long subCategoryId) {
	    if (isSubCategoryUsed(subCategoryId)) {
	        throw new RuntimeException("SubCategory is in use by a project and cannot be deleted.");
	    }
	    subCategoryRepository.deleteById(subCategoryId);
	}

	public boolean isSubCategoryUsed(Long subCategoryId) {
		return projectRepository.existsBySubCategory_Id(subCategoryId);
	}
}
