package com.example.portfolio.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.portfolio.model.Category;
import com.example.portfolio.repository.CategoryRepository;

@Service
public class CategoryService {
	
	private CategoryRepository categoryRepository;

	public List<Category> getCategory() {
		
		return categoryRepository.findAll();
	}

}
