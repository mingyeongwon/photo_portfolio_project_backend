package com.example.portfolio.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.portfolio.model.Category;
import com.example.portfolio.model.SubCategory;
import com.example.portfolio.repository.CategoryRepository;
import com.example.portfolio.repository.SubCategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private SubCategoryRepository subCategoryRepository;

	
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}
	
	// 여러 카테고리와 서브카테고리 저장
	@Transactional
	public void createCategories(List<Category> categories) {
		for (Category category : categories) {
			// 카테고리 저장
			categoryRepository.save(category);

			// 카테고리의 서브카테고리 저장
			for (SubCategory subCategory : category.getSubCategories()) {
				subCategory.setCategory(category);
				subCategoryRepository.save(subCategory);
			}
		}
	}
	
	
}
