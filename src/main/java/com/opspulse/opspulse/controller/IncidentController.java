package com.opspulse.opspulse.controller;
import com.opspulse.opspulse.dto.CreateIncidentRequest;
import com.opspulse.opspulse.dto.IncidentResponse;
import com.opspulse.opspulse.dto.UpdateStatusRequest;
import com.opspulse.opspulse.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService){
        this.incidentService = incidentService;
    }

    @GetMapping
    public List<IncidentResponse> listIncidents(Authentication authentication){
        UUID tenantId=(UUID)authentication.getDetails();
        return incidentService.getIncidentsForTenant(tenantId);
    }

    @GetMapping("/{id}")
    public IncidentResponse getIncident(Authentication authentication,@PathVariable UUID id){
      UUID tenantId=(UUID)authentication.getDetails();
      return  incidentService.getIncident(tenantId,id);
    }

    @PostMapping
    public IncidentResponse createIncident(
            @Valid @RequestBody CreateIncidentRequest request,
            Authentication authentication){
        UUID tenantId=(UUID)authentication.getDetails();
        UUID userId=(UUID)authentication.getPrincipal();
        return incidentService.createIncident(request, tenantId, userId);
    }

    @PatchMapping("/{id}")
    public IncidentResponse updateStatus(@PathVariable UUID id,
                                         @Valid @RequestBody UpdateStatusRequest request,Authentication authentication){
        UUID tenantId=(UUID)authentication.getDetails();
        return incidentService.updateStatus(id,tenantId,request.getStatus());
    }

    @PatchMapping("/{id}/ack")
    public IncidentResponse  setacknowledgedAt(@PathVariable UUID id, Authentication authentication){
        UUID tenantId=(UUID)authentication.getDetails();
        return incidentService.setacknowledgedAt(id,tenantId);
    }
}