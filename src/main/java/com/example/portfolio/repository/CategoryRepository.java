package com.example.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.portfolio.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
