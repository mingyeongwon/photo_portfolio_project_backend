package com.example.portfolio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio.model.Category;
import com.example.portfolio.service.CategoryService;

@RestController
@RequestMapping("/api")
public class ProjectController {

	@Autowired
	private CategoryService categoryService;
	
	//카테고리 페이지 데이터 전달
	@GetMapping("/category")
	public List<Category> getAllCategories() {
		return categoryService.getAllCategories();
	}
	
	//카테고리 생성
    @PostMapping("/categories")
    public void createCategories(@RequestBody List<Category> categories) {
        categoryService.createCategories(categories);
    }
}
