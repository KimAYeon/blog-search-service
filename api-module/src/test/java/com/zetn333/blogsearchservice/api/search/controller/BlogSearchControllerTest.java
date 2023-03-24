package com.zetn333.blogsearchservice.api.search.controller;

import com.zetn333.blogsearchservice.api.common.constansts.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class BlogSearchControllerTest {

    @Autowired
    MockMvc mvc;

    @Value("${api.prefix.v1}")
    String restApiPrefix;

    @Test
    void searchBlogTest() throws Exception {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("searchWord", "방탄소년단");
        params.add("page.pageNumber", "1");
        params.add("page.pageSize", "10");
        params.add("page.sort", "accuracy");

        // when // then
        mvc.perform(get("/" + restApiPrefix + "/blog/search").params(params))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.page.pageNumber").value(1))
                .andExpect(jsonPath("$.page.pageSize").value(10));
    }

    @Test
    void searchBlogParameterTest() throws Exception {
        // given - 필수값 X
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("searchWord", "");
        // when // then - INVALID_INPUT_PARAMETER_NOTEMPTY Exception 발생
        mvc.perform(get("/" + restApiPrefix + "/blog/search").params(params))
                .andExpect(status().is(ErrorCode.INVALID_INPUT_PARAMETER_NOTEMPTY.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_INPUT_PARAMETER_NOTEMPTY.getCode()))
                .andExpect(jsonPath("$.errorMessage").value(ErrorCode.INVALID_INPUT_PARAMETER_NOTEMPTY.getMessage()));

        // given - 필수값 O / 선택값 O / 유효성 X (MAX)
        params.set("searchWord", "아이유");
        params.set("page.pageNumber", "100");
        // when // then - INVALID_INPUT_PARAMETER_MAX Exception 발생
        mvc.perform(get("/" + restApiPrefix + "/blog/search").params(params))
                .andExpect(status().is(ErrorCode.INVALID_INPUT_PARAMETER_MAX.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_INPUT_PARAMETER_MAX.getCode()))
                .andExpect(jsonPath("$.errorMessage").value(ErrorCode.INVALID_INPUT_PARAMETER_MAX.getMessage()));

        // given - 필수값 O / 선택값 O / 유효성 X (MIN)
        params.set("page.pageNumber", "0");
        // when // then - INVALID_INPUT_PARAMETER_MIN Exception 발생
        mvc.perform(get("/" + restApiPrefix + "/blog/search").params(params))
                .andExpect(status().is(ErrorCode.INVALID_INPUT_PARAMETER_MIN.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_INPUT_PARAMETER_MIN.getCode()))
                .andExpect(jsonPath("$.errorMessage").value(ErrorCode.INVALID_INPUT_PARAMETER_MIN.getMessage()));

        // given - 필수값 O / 선택값 O / 유효성 X (Pattern)
        params.set("page.pageNumber", "10");
        params.set("page.sort", "acc");
        // when // then - INVALID_INPUT_PARAMETER_FORMAT Exception 발생
        mvc.perform(get("/" + restApiPrefix + "/blog/search").params(params))
                .andExpect(status().is(ErrorCode.INVALID_INPUT_PARAMETER_FORMAT.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_INPUT_PARAMETER_FORMAT.getCode()))
                .andExpect(jsonPath("$.errorMessage").value(ErrorCode.INVALID_INPUT_PARAMETER_FORMAT.getMessage()));
    }

    @Test
    void selectHotKeywordsTest() throws Exception {
        // given - 초기 데이터를 위해 검색 API 10번 호출
        for (int i=0; i<10; i++) {
            searchBlogTest();
        }
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("searchWord", "방탄소년단");
        params.add("page.pageNumber", "1");
        params.add("page.pageSize", "10");
        params.add("page.sort", "accuracy");

        // when //then
        mvc.perform(get("/" + restApiPrefix + "/blog/search/hot-keywords"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.list[0].searchCount").value(10));
    }
    
}
