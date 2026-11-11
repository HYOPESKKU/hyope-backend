package com.hyope.hyopeserver.dto.emergencyalert;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmergencyAlertResponse {

    @JsonProperty("guardian_phone_number")
    private String guardianPhoneNumber;

    @JsonProperty("guardian_name")
    private String guardianName;

    @JsonProperty("recipient_name")
    private String recipientName;
}
