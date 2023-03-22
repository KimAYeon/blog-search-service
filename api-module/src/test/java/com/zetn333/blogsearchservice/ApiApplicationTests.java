package com.zetn333.blogsearchservice;

import com.zetn333.blogsearchservice.api.common.dto.PageRequest;
import com.zetn333.blogsearchservice.api.search.dto.SearchBlogRequest;
import com.zetn333.blogsearchservice.api.search.dto.SearchBlogResponse;
import com.zetn333.blogsearchservice.api.search.service.BlogSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.querydsl.QPageRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApiApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void searchBlogTest() {
		SearchBlogRequest searchBlogRequest = new SearchBlogRequest();
		searchBlogRequest.setSearchWord("방탄소년단");
		PageRequest pageRequest = new PageRequest(1, 10, "accuracy");
		searchBlogRequest.setPage(pageRequest);

		//SearchBlogResponse searchBlogResponse = blogSearchService.searchBlog(searchBlogRequest);

		//assertNotNull(searchBlogResponse);
	}

}
