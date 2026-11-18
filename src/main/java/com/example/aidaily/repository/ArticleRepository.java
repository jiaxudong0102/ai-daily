package com.example.aidaily.repository;



import com.example.aidaily.common.AiStatus;
import com.example.aidaily.entity.ArticleEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
boolean existsByUrl(String url);
    List<ArticleEntity> findTop20ByOrderByIdDesc();
    List<ArticleEntity> findByAiStatus(AiStatus aiStatus, Pageable pageable);
    @Query("""
            select a from ArticleEntity a
            where a.text is not null and trim(a.text) <> ''
              and (
                a.aiStatus is null
                or a.aiStatus = :pending
                or (a.aiStatus = :failed and coalesce(a.retryCount, 0) < :maxRetries)
              )
            order by a.id asc
            """)
    List<ArticleEntity> findAiCandidates(
            @Param("pending") AiStatus pending,
            @Param("failed") AiStatus failed,
            @Param("maxRetries") long maxRetries,
            Pageable pageable);
    @Query("""
    select a from ArticleEntity a
            where a.text is not null and trim(a.text) <> ''
              and (
                a.aiStatus = :success
                and a.collectDate= :time
              )
            order by a.collectDate ,a.interestScore desc, a.id desc



""")
    List<ArticleEntity> findAiCandidate(
            @Param("success") AiStatus success,
            @Param("time") LocalDate localDate
    );
}
