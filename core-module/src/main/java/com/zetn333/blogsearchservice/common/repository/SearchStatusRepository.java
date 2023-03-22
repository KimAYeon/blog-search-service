package com.zetn333.blogsearchservice.common.repository;

import com.zetn333.blogsearchservice.common.entity.SearchStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SearchStatusRepository extends JpaRepository<SearchStatusEntity, Long> {

    /**
     * 검색 키워드로 검색 현황 엔티티 조회
     * @param searchKeyword 검색 키워드
     * @return SearchStatusEntity 조회된 검색 현황 엔티티
     */
    SearchStatusEntity findBySearchKeyword(String searchKeyword);

    /**
     * 검색 키워드를 포함하는 검색 현황 엔티티 조회
     * @param searchKeyword 검색 키워드
     * @return SearchStatusEntity 조회된 검색 현황 엔티티
     */
    List<SearchStatusEntity> findBySearchKeywordLike(String searchKeyword);

    /**
     * 검색 횟수가 많은 순으로 검색 키워드 10개 조회
     * @return List<SearchStatusEntity> 조회된 검색 현황 정보
     */
    @Query(value = "SELECT * FROM SEARCH_STATUS ORDER BY search_count DESC LIMIT 10", nativeQuery = true)
    List<SearchStatusEntity> findTop10BySearchCountDesc();

}
