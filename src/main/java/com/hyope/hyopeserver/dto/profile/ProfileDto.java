package com.hyope.hyopeserver.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ProfileDto(
        List<String> hobbies,
        @JsonProperty("recent_events") List<RecentEventDto> recentEvents,
        String address
) {}