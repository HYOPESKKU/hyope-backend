package com.hyope.hyopeserver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 우리가 직접 남기고 싶은 로그를 표시하는 애노테이션. AppLogAspect가 이 애노테이션이 붙은
 * 메서드의 진입/종료/실패를 "[APP]" 태그로 자동 로깅한다 — Hibernate SQL 등 프레임워크
 * 로그와 섞여도 grep "\[APP\]" 한 번으로 우리가 남긴 로그만 걸러낼 수 있다.
 *
 * 인자/반환값이 그대로 로그에 찍히니, 비밀번호 해시 등 민감한 값을 다루는 메서드엔 붙이지 말 것.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AppLog {
    String value() default "";
}
