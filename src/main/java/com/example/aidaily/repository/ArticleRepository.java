package com.example.aidaily.repository;



import com.example.aidaily.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
boolean existsByUrl(String url);
    List<ArticleEntity> findTop20ByOrderByIdDesc();
}