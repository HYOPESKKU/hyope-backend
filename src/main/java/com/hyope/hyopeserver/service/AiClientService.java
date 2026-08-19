package com.hyope.hyopeserver.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hyope.hyopeserver.dto.profile.ProfileDto;
import com.hyope.hyopeserver.entity.CallSchedule;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.enums.Status;
import com.hyope.hyopeserver.repository.CallScheduleRepository;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Consumer;

@Service
@Slf4j
public class AiClientService {

    private final CallScheduleRepository callScheduleRepository;
    private final RecipientProfileService recipientProfileService;
    private final WebClient webClient;

    public AiClientService(CallScheduleRepository callScheduleRepository,
                           RecipientProfileService recipientProfileService,
                           @Value("${internal.api-key}") String internalApiKey,
                           @Value("${ai.server.base-url:http://localhost:8000}") String aiServerBaseUrl) {
        this.callScheduleRepository = callScheduleRepository;
        this.recipientProfileService = recipientProfileService;
        this.webClient = WebClient.builder()
                .baseUrl(aiServerBaseUrl)
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
        doRequestCall(recipient, error -> {
            log.error("AI 서버 호출 최종 실패 (3회 재시도 소진): recipientId={}, error={}", recipient.getId(), error.getMessage());
            schedule.updateLastCallStatus(Status.FAILED);
            callScheduleRepository.save(schedule);
        });
    }

    // * "지금 바로 통화걸기"처럼 예약(CallSchedule)과 무관하게 즉시 거는 통화용 — requestCall(CallSchedule)과
    // * 같은 전송 로직(doRequestCall)을 재사용하되, 실패 시 CallSchedule.lastCallStatus를 갱신하지 않는다.
    // * 여기에 미저장(transient) CallSchedule을 새로 만들어 넘기면 실패 시 callScheduleRepository.save가
    // * id=null인 채로 저장돼 유령 스케줄 행이 생기므로, 그 경로를 아예 타지 않게 분리했다.
    public void requestCall(Recipient recipient) {
        doRequestCall(recipient, error ->
                log.error("AI 서버 호출 최종 실패 (3회 재시도 소진, 즉시 통화): recipientId={}, error={}",
                        recipient.getId(), error.getMessage()));
    }

    private void doRequestCall(Recipient recipient, Consumer<Throwable> onFinalFailure) {
        ProfileDto profile = recipientProfileService.readProfile(recipient.getId());

        webClient.post()
                .uri("/call")
                .bodyValue(new CallRequestBody(
                        recipient.getId(),
                        recipient.getPhoneNumber(),
                        profile))
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .doAfterRetry(signal -> log.warn("AI 서버 호출 재시도 {}/3: recipientId={}, error={}",
                                signal.totalRetries() + 1, recipient.getId(), signal.failure().getMessage())))
                .doOnError(onFinalFailure::accept)
                .subscribe(
                        null,
                        error -> {}
                );
    }

    private record CallRequestBody(
            @JsonProperty("recipient_id") Long recipientId,
            @JsonProperty("phone_number") String phoneNumber,
            ProfileDto profile
    ) {}
}