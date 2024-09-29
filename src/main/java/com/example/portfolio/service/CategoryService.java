package com.example.portfolio.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.portfolio.model.Category;  // 올바른 Category 엔티티 임포트
import com.example.portfolio.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    // 전체 카테고리 리스트 조회
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
}
