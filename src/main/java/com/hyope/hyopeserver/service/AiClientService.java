package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.entity.Recipient;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Service
@Slf4j
public class AiClientService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8000")
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create()
                            .responseTimeout(Duration.ofSeconds(60))
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            ))
            .build();

    public void requestCall(Recipient recipient) {
        try {
            webClient.post()
                    .uri("/call")  // FastAPI 엔드포인트 (협의 후 수정)
                    .bodyValue(new CallRequestBody(recipient.getId(), recipient.getPhoneNumber()))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .subscribe(
                            null,
                            error -> log.error("AI 서버 호출 실패: recipientId={}, error={}", recipient.getId(), error.getMessage())
                    );
        } catch (Exception e) {
            log.error("AI 서버 호출 중 예외 발생: recipientId={}", recipient.getId(), e);
        }
    }

    // FastAPI로 보내는 요청 body (내부 record)
    private record CallRequestBody(Long recipientId, String phoneNumber) {}
}