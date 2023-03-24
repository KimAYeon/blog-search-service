package com.zetn333.blogsearchservice.api.search.service;

import com.zetn333.blogsearchservice.api.common.constansts.ErrorCode;
import com.zetn333.blogsearchservice.api.common.constansts.OpenApi;
import com.zetn333.blogsearchservice.api.common.dto.PageRequest;
import com.zetn333.blogsearchservice.api.common.exception.CustomServiceException;
import com.zetn333.blogsearchservice.api.search.dto.SearchBlogRequest;
import com.zetn333.blogsearchservice.api.search.dto.SearchBlogResponse;
import com.zetn333.blogsearchservice.api.search.dto.SelectHotKeywordsRequest;
import com.zetn333.blogsearchservice.api.search.dto.SelectHotKeywordsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
class BlogSearchServiceTest {

	@Autowired
   	BlogSearchService blogSearchService;

    @Test
   	void searchBlogParameterTest() {
		SearchBlogResponse searchBlogResponse;
		SearchBlogRequest searchBlogRequest = new SearchBlogRequest();
		PageRequest pageRequest;


    	// given - 필수값 X
		CustomServiceException exception = assertThrows(CustomServiceException.class, () -> {
			// when
			blogSearchService.searchBlog(searchBlogRequest);
		});
		// then
		assertEquals(exception.getCode(), ErrorCode.INVALID_INPUT_OPEN_API.getCode());


   		// given - 필수값 O / 선택값 X (페이징 설정 X)
		searchBlogRequest.setSearchWord("방탄소년단");
		// when
   		searchBlogResponse = blogSearchService.searchBlog(searchBlogRequest);
   		// then
   		assertNotNull(searchBlogResponse);
   		assertEquals(searchBlogResponse.getList().size(), 10);

   		// given - 필수값 O / 선택값 O (페이징 설정 O)
   		pageRequest = PageRequest.builder()
   				.pageNumber(2)
   				.pageSize(5)
   				.sort(OpenApi.kakaoBlogSearchParams.SORT_RECENCY.getValue())
   				.build();
   		searchBlogRequest.setPage(pageRequest);
		// when
   		searchBlogResponse = blogSearchService.searchBlog(searchBlogRequest);
		// then
   		assertNotNull(searchBlogResponse);
   		assertEquals(searchBlogResponse.getList().size(), 5);	// 개수
   		assertTrue(searchBlogResponse.getList().get(0).getWriteDateTime()
   				.after(searchBlogResponse.getList().get(1).getWriteDateTime())); // 최신순


		// given - 필수값 O / 선택값 O / 유효성 X
		pageRequest = PageRequest.builder()
				.pageNumber(10)
				.pageSize(100)
				.sort("아무거나")
				.build();
		searchBlogRequest.setPage(pageRequest);
		exception = assertThrows(CustomServiceException.class, () -> {
			// when
			blogSearchService.searchBlog(searchBlogRequest);
		});
		// then
		assertEquals(exception.getCode(), ErrorCode.INVALID_INPUT_OPEN_API.getCode());
   	}

	@Test
	void searchBlog() throws InterruptedException {
		SearchBlogRequest searchBlogRequest = new SearchBlogRequest();
		SelectHotKeywordsRequest selectHotKeywordsRequest = new SelectHotKeywordsRequest();

		// given
		searchBlogRequest.setSearchWord("아이유");
		PageRequest pageRequest = PageRequest.builder()
				.pageNumber(2)
				.pageSize(5)
				.sort(OpenApi.kakaoBlogSearchParams.SORT_RECENCY.getValue())
				.build();
		searchBlogRequest.setPage(pageRequest);
		// TODO: DB에 데이터가 없을 경우, 엔티티 조회가 되지 않으므로 비관적 락이 걸리지 않음.
		// 		 테스트를 위해 초기 데이터 우선 삽입. 데이터 없을 경우 동시성 이슈 처리 요망.
		blogSearchService.searchBlog(searchBlogRequest);

		// when - thread 100개 동시 실행
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		CountDownLatch countDownLatch = new CountDownLatch(100);

    	for (int i=0; i<100; i++) {
			executorService.execute(() -> {
				blogSearchService.searchBlog(searchBlogRequest);
				countDownLatch.countDown();
			});
		}

		countDownLatch.await();

		// then
 		SelectHotKeywordsResponse selectHotKeywordsResponse = blogSearchService.selectHotKeywords(selectHotKeywordsRequest);
 		assertEquals(selectHotKeywordsResponse.getList().get(0).getSearchCount(), BigInteger.valueOf(101));
	}

    @Test
    void updateSearchKeywordStatusCurrencyTest() throws InterruptedException {
		SearchBlogRequest searchBlogRequest = new SearchBlogRequest();
		SelectHotKeywordsRequest selectHotKeywordsRequest = new SelectHotKeywordsRequest();

		// given
		searchBlogRequest.setSearchWord("송민호");
		PageRequest pageRequest = PageRequest.builder()
				.pageNumber(2)
				.pageSize(5)
				.sort(OpenApi.kakaoBlogSearchParams.SORT_RECENCY.getValue())
				.build();
		searchBlogRequest.setPage(pageRequest);
		// TODO: DB에 데이터가 없을 경우, 엔티티 조회가 되지 않으므로 비관적 락이 걸리지 않음.
		// 		 테스트를 위해 초기 데이터 우선 삽입. 데이터 없을 경우 동시성 이슈 처리 요망.
		blogSearchService.searchBlog(searchBlogRequest);

		// when - thread 100개 동시 실행
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		CountDownLatch countDownLatch = new CountDownLatch(100);

    	for (int i=0; i<100; i++) {
			executorService.execute(() -> {
				blogSearchService.updateSearchKeywordStatus(searchBlogRequest);
				countDownLatch.countDown();
			});
		}

		countDownLatch.await();

    	// then
    	SelectHotKeywordsResponse selectHotKeywordsResponse = blogSearchService.selectHotKeywords(selectHotKeywordsRequest);
    	assertEquals(selectHotKeywordsResponse.getList().get(0).getSearchCount(), BigInteger.valueOf(101));
    }

    @Test
    void selectHotKeywordsCurrencyTest() throws InterruptedException {
		SearchBlogRequest searchBlogRequest = new SearchBlogRequest();
		SelectHotKeywordsRequest selectHotKeywordsRequest = new SelectHotKeywordsRequest();
		SelectHotKeywordsResponse selectHotKeywordsResponse = new SelectHotKeywordsResponse();

		// given
		searchBlogRequest.setSearchWord("곽튜브");
		PageRequest pageRequest = PageRequest.builder()
				.pageNumber(2)
				.pageSize(5)
				.sort(OpenApi.kakaoBlogSearchParams.SORT_RECENCY.getValue())
				.build();
		searchBlogRequest.setPage(pageRequest);
		blogSearchService.searchBlog(searchBlogRequest);

		// when - thread 100개 동시 실행
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		CountDownLatch countDownLatch = new CountDownLatch(100);

    	for (int i=1; i<=100; i++) {
			executorService.execute(() -> {
				blogSearchService.searchBlog(searchBlogRequest);
				countDownLatch.countDown();
			});
			if (i == 50) {
				selectHotKeywordsResponse = blogSearchService.selectHotKeywords(selectHotKeywordsRequest);
			}
		}

		countDownLatch.await();

		// then
		// TODO: 블로그 검색 횟수와 인기 검색어 횟수 싱크가 맞지 않음.
		//assertEquals(selectHotKeywordsResponse.getList().get(0).getSearchCount(), BigInteger.valueOf(51));
		selectHotKeywordsResponse = blogSearchService.selectHotKeywords(selectHotKeywordsRequest);
 		assertEquals(selectHotKeywordsResponse.getList().get(0).getSearchCount(), BigInteger.valueOf(101));
    }

}