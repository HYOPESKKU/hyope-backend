package com.hyope.hyopeserver.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record RecentEventDto(
        String id,
        String text,
        @JsonProperty("added_at") LocalDate addedAt
) {}