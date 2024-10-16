package com.example.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.portfolio.model.SubCategory;
import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    // 카테고리 ID로 서브카테고리 목록 조회
    List<SubCategory> findByCategory_Id(Long categoryId);

    // 카테고리 ID로 서브카테고리가 존재하는지 확인
    boolean existsByCategory_Id(Long categoryId);
}
