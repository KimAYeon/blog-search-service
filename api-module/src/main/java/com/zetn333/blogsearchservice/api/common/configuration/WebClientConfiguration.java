package com.zetn333.blogsearchservice.api.common.configuration;

import com.zetn333.blogsearchservice.api.common.constansts.ErrorCode;
import com.zetn333.blogsearchservice.api.common.exception.CustomServiceException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * Open API 사용을 위한 WebClient 설정
 */
@Slf4j
@Configuration
public class WebClientConfiguration {

    /**
     * WebClient 기본 설정 및 빈 생성
     * @param httpClient
     * @return WebClient
     */
    @Bean
    public WebClient commonWebClient(HttpClient httpClient) {
        ExchangeFilterFunction errorFilter = ExchangeFilterFunction
                .ofResponseProcessor( clientResponse -> exchangeFilterResponseProcessor(clientResponse));

        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(errorFilter)
                .build();
    }

    /**
     * HTTP 통신 타임 아웃 시간 설정
     * @return HttpClient
     */
    @Bean
    public HttpClient defaultHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(10))  // 읽기 타임 아웃 시간
                        .addHandlerLast(new WriteTimeoutHandler(10)));   // 쓰기 타임 아웃 시간
    }

    /**
     * 커넥션 풀 관련 정보 설정 - 추후 필요에 따라 사용
     * @return ConnectionProvider
     */
    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("http-pool")
                .maxConnections(100)					     // connection pool 개수
                .pendingAcquireTimeout(Duration.ofMillis(0)) // 커넥션 풀에서 커넥션을 얻기 위해 기다리는 최대 시간
                .pendingAcquireMaxCount(-1) 				 // 커넥션 풀에서 커넥션을 가져오는 시도 횟수 (-1: no limit)
                .maxIdleTime(Duration.ofMillis(2000L)) 		 // 커넥션 풀에서 idle 상태의 커넥션을 유지하는 시간
                .build();
    }

    /**
     * WebClient 사용하여 Open API 호출 시 발생하는 에러 처리를 위한 필터
     * @param response
     * @return Mono<ClientResponse>
     */
    private Mono<ClientResponse> exchangeFilterResponseProcessor(ClientResponse response) {
        HttpStatus status = response.statusCode();

        if (status.is5xxServerError()) {
            // 5XX 시스템 에러
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new CustomServiceException(ErrorCode.SERVER_ERROR_OPEN_API)));
        } else if (status.is4xxClientError()) {
            // 4XX 요청 에러
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new CustomServiceException(ErrorCode.INVALID_INPUT_OPEN_API)));
        }

        return Mono.just(response);
    }

}
