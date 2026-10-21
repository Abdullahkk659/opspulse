package com.opspulse.opspulse.dto;
import com.opspulse.opspulse.entity.IncidentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class UpdateStatusRequest {
    @NotNull
    private IncidentStatus status;

}
