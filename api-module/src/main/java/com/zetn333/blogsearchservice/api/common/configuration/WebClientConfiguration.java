package com.zetn333.blogsearchservice.api.common.configuration;

import com.zetn333.blogsearchservice.common.exception.ErrorCode;
import com.zetn333.blogsearchservice.common.exception.ServiceException;
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

import javax.sql.rowset.serial.SerialException;
import java.time.Duration;

/**
 * 외부 API 사용을 위한 WebClient 설정
 */
@Slf4j
@Configuration
public class WebClientConfiguration {

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

    @Bean
    public HttpClient defaultHttpClient(ConnectionProvider provider) {
        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5)) //읽기시간초과 타임아웃
                        .addHandlerLast(new WriteTimeoutHandler(5)));
    }

    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("http-pool")
                .maxConnections(100)					     // connection pool의 갯수
                .pendingAcquireTimeout(Duration.ofMillis(0)) //커넥션 풀에서 커넥션을 얻기 위해 기다리는 최대 시간
                .pendingAcquireMaxCount(-1) 				//커넥션 풀에서 커넥션을 가져오는 시도 횟수 (-1: no limit)
                .maxIdleTime(Duration.ofMillis(2000L)) 		//커넥션 풀에서 idle 상태의 커넥션을 유지하는 시간
                .build();
    }

    /**
     * WebClient 사용 시, 에러 처리를 위한 필터
     * @param response
     * @return
     */
    private Mono<ClientResponse> exchangeFilterResponseProcessor(ClientResponse response) {
        HttpStatus status = response.statusCode();

        if (status.is5xxServerError()) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ServiceException(ErrorCode.INVALID_INPUT_EXTERNAL_API)));
        } else if (status.is4xxClientError()) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR)));
        }

        return Mono.just(response);
    }

}
