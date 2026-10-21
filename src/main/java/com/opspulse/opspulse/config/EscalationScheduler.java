package com.opspulse.opspulse.config;

import com.opspulse.opspulse.dto.IncidentResponse;
import com.opspulse.opspulse.entity.Incident;
import com.opspulse.opspulse.entity.IncidentStatus;
import com.opspulse.opspulse.repository.IncidentRepository;
import com.opspulse.opspulse.service.IncidentService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.opspulse.opspulse.service.AlertService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@Transactional
public class EscalationScheduler{
    private final IncidentRepository incidentRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final IncidentService incidentService;
    private final AlertService alertService;

    public EscalationScheduler(IncidentRepository incidentRepository,
                               SimpMessagingTemplate simpMessagingTemplate,

                               IncidentService incidentService,AlertService alertService) {
        this.incidentRepository = incidentRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.incidentService = incidentService;
        this.alertService=alertService;
    }

    @Scheduled(fixedRate = 30000)
    public void escalateStaleIncidents() {
        Instant cutoff = Instant.now().minusSeconds(30);
        List<Incident> stale = incidentRepository
                .findByStatusAndEscalationLevelAndAcknowledgedAtIsNullAndCreatedAtBefore(IncidentStatus.OPEN, 0, cutoff);

        for (Incident incident : stale) {
            incident.setEscalationLevel(1);
            Incident saved = incidentRepository.save(incident);
            IncidentResponse response = incidentService.toResponse(saved);

            UUID tenantId = incident.getTenant().getId();
            simpMessagingTemplate.convertAndSend("/topic/incident/" + tenantId, response);
            alertService.sendEscalationAlert(saved);

            System.out.println("Escalated incident " + incident.getId() + " to level 1");
        }
    }
}
