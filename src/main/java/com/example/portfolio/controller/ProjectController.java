package com.example.portfolio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio.model.Category;
import com.example.portfolio.service.CategoryService;

@RestController
@RequestMapping("/api")
public class ProjectController {

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/category")
	public List<Category> getAllCategories() {
		return categoryService.getAllCategories();
	}
}
