package com.hyope.hyopeserver.entity.enums;

public enum NotificationReason {
    HIGH_RISK,      // 절대 위험도 기준 초과 (WARNING 이상)
    SUDDEN_SPIKE,   // 전일 대비 급증 (1.5점 이상)
    BOTH            // 둘 다 해당
}