package com.example.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.portfolio.model.SubCategory;
import java.util.List;


@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
	List<SubCategory> findByCategory_id(Long categoryId);
}
