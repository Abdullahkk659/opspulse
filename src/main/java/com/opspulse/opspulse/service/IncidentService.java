package com.opspulse.opspulse.service;
import com.opspulse.opspulse.dto.CreateIncidentRequest;
import com.opspulse.opspulse.dto.IncidentResponse;
import com.opspulse.opspulse.entity.Incident;
import com.opspulse.opspulse.entity.IncidentStatus;
import com.opspulse.opspulse.entity.Tenant;
import com.opspulse.opspulse.entity.User;
import com.opspulse.opspulse.repository.IncidentRepository;
import com.opspulse.opspulse.repository.UserRepository;
import com.opspulse.opspulse.repository.TenantRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpmessagetemplate;

    public IncidentService(IncidentRepository incidentRepository, TenantRepository tenantRepository, UserRepository userRepository, SimpMessagingTemplate simpMessageTemplate){
        this.incidentRepository=incidentRepository;
        this.tenantRepository=tenantRepository;
        this.userRepository=userRepository;
        this.simpmessagetemplate=simpMessageTemplate;
    }
    public List<IncidentResponse> getIncidentsForTenant(UUID tenantId){
        return incidentRepository.findByTenantId(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public IncidentResponse getIncident(UUID incidentId,UUID tenantId){
        Incident incident= incidentRepository.findByIdAndTenantId(incidentId,tenantId)
                .orElseThrow(()->new RuntimeException("Incident not found!"));
        return toResponse(incident);
    }
    public IncidentResponse toResponse(Incident incident){
        IncidentResponse response=new IncidentResponse();
        response.setId(incident.getId());
        response.setTitle(incident.getTitle());
        response.setDescription(incident.getDescription());
        response.setSeverity(incident.getSeverity());
        response.setStatus(incident.getStatus());
        response.setCreatedAt(incident.getCreatedAt());
        response.setUpdatedAt(incident.getUpdatedAt());
        response.setTenantName(incident.getTenant().getName());
        response.setCreatedByName(incident.getCreated_By().getName());
        response.setEscalationLevel(incident.getEscalationLevel());
        response.setAcknowledgedAt(incident.getAcknowledgedAt());
        return response;
    }
    public IncidentResponse createIncident(CreateIncidentRequest request, UUID tenantId, UUID userId){
        Tenant tenant=tenantRepository.findById(tenantId)
                        .orElseThrow(()->new RuntimeException("Tenant Id does not exist!"));
        User creator=userRepository.findById(userId)
                        .orElseThrow(()->new RuntimeException(("User id does not exist!")));
        Incident incident=new Incident();
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setSeverity(request.getSeverity());
        incident.setTenant(tenant);
        incident.setCreated_By(creator);
        Incident saved= incidentRepository.save(incident);
        IncidentResponse response=toResponse(saved);
        simpmessagetemplate.convertAndSend("/topic/incident/"+tenantId,response);
        return response;


    }

    public IncidentResponse updateStatus(UUID incidentId, UUID tenantId, IncidentStatus newStatus) {
        Incident incident = incidentRepository.findByIdAndTenantId(incidentId, tenantId)
                .orElseThrow(() -> new RuntimeException("Incident not found!"));
        incident.setStatus(newStatus);
        Incident saved = incidentRepository.save(incident);
        IncidentResponse response=toResponse(saved);
        simpmessagetemplate.convertAndSend("/topic/incident/"+tenantId,response);
        return response;
    }
    public IncidentResponse setacknowledgedAt(UUID incidentId,UUID tenantId){
        Incident incident=incidentRepository.findByIdAndTenantId(incidentId,tenantId).orElseThrow(()->new RuntimeException("Incident not found!"));
        incident.setAcknowledgedAt(Instant.now());
        Incident saved=incidentRepository.save(incident);
        IncidentResponse response=toResponse(saved);
        simpmessagetemplate.convertAndSend("/topic/incident/"+tenantId,response);
        return response;

    }
    public List<IncidentResponse> findByTenantIdAndStatus(UUID tenantId,IncidentStatus status ){
        return incidentRepository.findByTenantIdAndStatus(tenantId,status)
                .stream()
                .map(this::toResponse)
                .toList();
    }
























































}
