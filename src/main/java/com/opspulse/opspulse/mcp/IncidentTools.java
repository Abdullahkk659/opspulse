package com.opspulse.opspulse.mcp;

import com.opspulse.opspulse.dto.CreateIncidentRequest;
import com.opspulse.opspulse.dto.IncidentResponse;
import com.opspulse.opspulse.entity.IncidentStatus;
import com.opspulse.opspulse.repository.IncidentRepository;
import com.opspulse.opspulse.service.IncidentService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.UUID;


@Component
public class IncidentTools {

    private IncidentService incidentService;
    private IncidentRepository incidentRepository;
    public IncidentTools(IncidentRepository incidentRepository,IncidentService incidentService){
        this.incidentRepository=incidentRepository;
        this.incidentService=incidentService;
    }

    @McpTool(description = "Returns all currently open (unresolved) incidents for the tenant, including title, severity, status, and escalation level.")
    public List<IncidentResponse>listOpenIncidents(){
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID tenantId = UUID.fromString(jwt.getClaimAsString("tenantId"));
        return incidentService.findByTenantIdAndStatus(tenantId, IncidentStatus.OPEN);
    }

    @McpTool(description = "Creates a new incident for the tenant. Use this to report a new operational issue or outage. Returns the created incident.")
    public IncidentResponse createIncidents(@McpToolParam CreateIncidentRequest request){
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID tenantId = UUID.fromString(jwt.getClaimAsString("tenantId"));
        UUID userId=UUID.fromString(jwt.getClaimAsString("userId"));
        return incidentService.createIncident(request,tenantId,userId);
    }

    @McpTool(description = "Updates the status of an existing incident (for example, to mark it resolved). Returns the updated incident.")
    public IncidentResponse updateIncidentStatus(@McpToolParam(description = "The new status to set. One of: <list your actual IncidentStatus enum values, e.g. OPEN, IN_PROGRESS, RESOLVED>.")  IncidentStatus status, @McpToolParam(description = "The UUID of the incident to update.") UUID id){
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID tenantId = UUID.fromString(jwt.getClaimAsString("tenantId"));
        return incidentService.updateStatus(id,tenantId,status);
    }
    @McpTool(description = "Acknowledges an incident, marking that someone has taken ownership. This freezes its auto-escalation. Returns the updated incident.")
    public IncidentResponse acknowledgeIncident(@McpToolParam(description = "The UUID of the incident to update.") UUID id){
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID tenantId = UUID.fromString(jwt.getClaimAsString("tenantId"));
        return incidentService.setacknowledgedAt(id,tenantId);
    }

}
