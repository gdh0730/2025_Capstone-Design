package com.poseman.backend.config;

import java.time.Duration;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
                // 1) 커넥션 매니저(pooling)
                PoolingHttpClientConnectionManager connManager = PoolingHttpClientConnectionManagerBuilder.create()
                                .setMaxConnTotal(100) // 전체 커넥션 풀 크기
                                .setMaxConnPerRoute(20) // 라우트(호스트별) 최대 크기
                                .build();

                // 2) 요청 설정 (타임아웃 등)
                RequestConfig requestConfig = RequestConfig.custom()
                                .setConnectTimeout(Timeout.ofSeconds(10)) // 연결 타임아웃
                                .setResponseTimeout(Timeout.ofSeconds(30)) // 응답 대기 타임아웃
                                .build();

                // 3) CloseableHttpClient 생성
                CloseableHttpClient httpClient = HttpClients.custom()
                                .setConnectionManager(connManager)
                                .setDefaultRequestConfig(requestConfig)
                                // 필요하다면 retry handler, keep-alive 전략 등을 추가 설정
                                .build();

                // 4) HttpComponentsClientHttpRequestFactory에 주입
                HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

                // 5) RestTemplateBuilder에 factory와 인터셉터를 연결
                return builder
                                .requestFactory(() -> factory)
                                .setConnectTimeout(Duration.ofSeconds(10))
                                .setReadTimeout(Duration.ofSeconds(30))
                                .additionalInterceptors((request, body, execution) -> {
                                        // (디버깅용) 실제 호출 URL 출력 (필요하다면 log.debug로 바꿀 것)
                                        System.out.println("--> Shinobi 호출 URL: " + request.getURI());
                                        return execution.execute(request, body);
                                })
                                .build();
        }
}
