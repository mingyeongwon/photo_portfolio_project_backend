package com.example.portfolio.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.portfolio.dto.CategoryCreateDto;
import com.example.portfolio.dto.CategoryDto;
import com.example.portfolio.dto.CategoryUpdateDto;
import com.example.portfolio.dto.SubCategoryCreateDto;
import com.example.portfolio.dto.SubCategoryDto;
import com.example.portfolio.dto.SubCategoryUpdateDto;
import com.example.portfolio.mapper.CategoryMapper;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.SubCategory;
import com.example.portfolio.repository.CategoryRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.SubCategoryRepository;

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
	
	@Cacheable(value = "category", key = "'categoryList'")
	  public List<CategoryDto> getCategoriesWithProjects() {
	        List<Category> categoriesWithProjects = projectRepository.findCategoriesWithProjects();
	        return categoriesWithProjects.stream()
	                .map(categoryMapper::categoryToDto) // Category 엔티티를 CategoryDto로 매핑
	                .collect(Collectors.toList());
	    }
	
	public List<SubCategoryDto> getSubCategoriesWithProjects(Long categoryId) {
		List<SubCategory> subCategories = projectRepository.findSubCategoriesWithProjects(categoryId);
		return subCategories.stream().map(this::subCategoryEntityToDto).toList();
	}

	@Transactional
	public CategoryCreateDto createCategories(CategoryCreateDto categoryCreateDto) {
		Category category = categoryMapper.createDtoToEntity(categoryCreateDto);
		return CategoryMapper.INSTANCE.categoryToCreateDto(categoryRepository.save(category));
	}

	@Transactional
	@CacheEvict(value = "category", key = "'categoryList'")
	public void deleteCategory(Long categoryId) {
		if (isCategoryUsed(categoryId)) {
			throw new RuntimeException("Category is in use by a project and cannot be deleted.");
		}
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found"));
		categoryRepository.delete(category);
	}

	@Transactional
	public boolean isCategoryUsed(Long categoryId) {
		return projectRepository.existsByCategory_Id(categoryId);
	}

	// DTO -> Entity 변환
//	private Category mapDtoToEntity(CategoryDto categoryDto) {
//		Category category = new Category();
//		category.setId(categoryDto.getId());
//		category.setName(categoryDto.getName());
//
//		List<SubCategory> subCategories = categoryDto.getSubCategories().stream().map(subCategoryDto -> {
//			SubCategory subCategory = new SubCategory();
//			subCategory.setId(subCategoryDto.getId());
//			subCategory.setName(subCategoryDto.getName());
//			subCategory.setCategory(category);
//			return subCategory;
//		}).toList();
//
//		category.setSubCategories(subCategories);
//		return category;
//	}

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

	@Transactional
	public SubCategoryCreateDto createSubCategory(Long categoryId, SubCategoryCreateDto subCategoryDto) {
		subCategoryDto.setCategoryId(categoryId);
		SubCategory subCategory = categoryMapper.createSubCategoryToSubCategory(subCategoryDto);
		return CategoryMapper.INSTANCE.createSubCategoryToSubCategoryDto(subCategoryRepository.save(subCategory));
	}

	@Transactional
	public void deleteSubCategory(Long subCategoryId) {
		if (isSubCategoryUsed(subCategoryId)) {
			throw new RuntimeException("SubCategory is in use by a project and cannot be deleted.");
		}
		subCategoryRepository.deleteById(subCategoryId);
	}

	@Transactional
	public boolean isSubCategoryUsed(Long subCategoryId) {
		return projectRepository.existsBySubCategory_Id(subCategoryId);
	}

	@Transactional
	@CacheEvict(value = "category", key = "'categoryList'")
	public void updateCategory(CategoryUpdateDto categoryUpdateDto) {
		Category category = categoryRepository.findById(categoryUpdateDto.getId()).orElseThrow(
				() -> new IllegalArgumentException("Category not found with id: " + categoryUpdateDto.getId()));
		category.setName(categoryUpdateDto.getName());
		// subCategories 리스트를 변경하지 않음
		categoryRepository.save(category);
	}

    // 서브 카테고리 수정
    @Transactional
    public void updateSubCategory(Long subCategoryId, SubCategoryUpdateDto subCategoryUpdateDto) {
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("SubCategory not found with id: " + subCategoryId));
        subCategory.setName(subCategoryUpdateDto.getName());
        subCategoryRepository.save(subCategory);
    }

}