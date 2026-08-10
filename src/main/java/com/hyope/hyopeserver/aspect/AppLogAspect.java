package com.hyope.hyopeserver.aspect;

import com.hyope.hyopeserver.annotation.AppLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class AppLogAspect {

    @Around("@annotation(appLog)")
    public Object logAround(ProceedingJoinPoint joinPoint, AppLog appLog) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());

        log.info("[APP] {}.{}({}) 시작 - {}", className, methodName, args, appLog.value());
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[APP] {}.{} 완료 ({}ms) - 반환값: {}", className, methodName, elapsed, result);
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.warn("[APP] {}.{} 실패 ({}ms) - {}", className, methodName, elapsed, ex.getMessage());
            throw ex;
        }
    }
}
