package com.opspulse.opspulse.dto;

import com.opspulse.opspulse.entity.IncidentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class IncidentResponse {

    private UUID id;
    private String title;
    private String description;
    private String severity;
    private IncidentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private String tenantName;
    private String createdByName;
    private int escalationLevel;
    private Instant acknowledgedAt;
}