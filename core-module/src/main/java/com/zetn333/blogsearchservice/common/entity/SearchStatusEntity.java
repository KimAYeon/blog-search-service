package com.zetn333.blogsearchservice.common.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="SEARCH_STATUS")
public class SearchStatusEntity extends BaseTimeEntity {

    @Builder
    public SearchStatusEntity(Long searchId, String searchKeyword, BigInteger searchCount, LocalDateTime lastSearchDateTime) {
        this.searchId = searchId;
        this.searchKeyword = searchKeyword;
        this.searchCount = searchCount;
        this.lastSearchDateTime = lastSearchDateTime;
    }

    /* 검색 아이디 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id")
    private Long searchId;

    /* 검색 키워드 */
    @Column(name = "search_keyword", unique = true, nullable = false)
    private String searchKeyword;

    /* 검색 횟수 */
    @Column(name = "search_count", nullable = false, columnDefinition = "BIGINT")
    private BigInteger searchCount;

    /* 최근 검색 일시 */
    @Column(name = "last_search_datetime")
    private LocalDateTime lastSearchDateTime;

    /**
     * 검색 횟수 증가
     */
    public void plusSearchCount() {
        this.searchCount = searchCount.add(BigInteger.ONE);
    }

    /**
     * 최근 검색 일시 업데이트
     */
    public void updateLastSearchDateTime() {
        this.lastSearchDateTime = LocalDateTime.now();
    }

}


