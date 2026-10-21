package com.opspulse.opspulse.repository;

import com.opspulse.opspulse.entity.Incident;
import com.opspulse.opspulse.entity.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.Instant;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {

    List<Incident> findByTenantId(UUID tenantId);
    List<Incident> findByStatusAndEscalationLevelAndCreatedAtBefore(
            IncidentStatus status, int escalationLevel, Instant cutoff);
    Optional<Incident> findByIdAndTenantId(UUID id, UUID tenantId);

    List<Incident> findByStatusAndEscalationLevelAndAcknowledgedAtIsNullAndCreatedAtBefore(IncidentStatus incidentStatus, int i, Instant cutoff);
    List<Incident> findByTenantIdAndStatus(UUID tenantId,IncidentStatus status);

}