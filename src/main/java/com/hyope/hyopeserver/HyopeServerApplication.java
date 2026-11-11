package com.hyope.hyopeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class HyopeServerApplication {
	public static void main(String[] args) {
		// JVM 기본 타임존을 명시적으로 고정한다 — 안 그러면 배포 환경(Railway 등)의 기본
		// 타임존(보통 UTC)을 따라가서, CallScheduler의 LocalTime.now()가 실제 한국 시간과
		// 어긋나 예약된 시각에 전화가 안 나가는 문제가 생긴다.
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(HyopeServerApplication.class, args);
	}
}