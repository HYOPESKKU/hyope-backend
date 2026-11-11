package com.hyope.hyopeserver.dto.emergencyalert;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class EmergencyAlertRequest {

    @JsonProperty("recipient_id")
    private Long recipientId;

    private String signal;

    private String severity;

    @JsonProperty("logic_data")
    private Map<String, Object> logicData;

    @JsonProperty("filed_at")
    private Double filedAt;
}
