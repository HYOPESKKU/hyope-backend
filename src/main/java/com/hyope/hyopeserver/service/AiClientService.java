package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.entity.CallSchedule;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.enums.Status;
import com.hyope.hyopeserver.repository.CallScheduleRepository;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@Slf4j
public class AiClientService {

    private final CallScheduleRepository callScheduleRepository;
    private final WebClient webClient;

    public AiClientService(CallScheduleRepository callScheduleRepository,
                           @Value("${internal.api-key}") String internalApiKey) {
        this.callScheduleRepository = callScheduleRepository;
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8000")
                .defaultHeader("X-API-KEY", internalApiKey)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(60))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                ))
                .build();
    }

    public void requestCall(CallSchedule schedule) {
        Recipient recipient = schedule.getRecipient();
        String guardianPhoneNumber = recipient.getGuardian() != null
                ? recipient.getGuardian().getPhoneNumber()
                : "";
        webClient.post()
                .uri("/call")
                .bodyValue(new CallRequestBody(recipient.getId(), recipient.getPhoneNumber(), guardianPhoneNumber))
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .doAfterRetry(signal -> log.warn("AI 서버 호출 재시도 {}/3: recipientId={}, error={}",
                                signal.totalRetries() + 1, recipient.getId(), signal.failure().getMessage())))
                .doOnError(error -> {
                    log.error("AI 서버 호출 최종 실패 (3회 재시도 소진): recipientId={}, error={}", recipient.getId(), error.getMessage());
                    schedule.updateLastCallStatus(Status.FAILED);
                    callScheduleRepository.save(schedule);
                })
                .subscribe(
                        null,
                        error -> {}
                );
    }

    private record CallRequestBody(Long recipientId, String phoneNumber, String guardianPhoneNumber) {}
}