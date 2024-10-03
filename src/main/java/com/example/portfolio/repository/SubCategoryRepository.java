package com.example.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.portfolio.model.SubCategory;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
}
