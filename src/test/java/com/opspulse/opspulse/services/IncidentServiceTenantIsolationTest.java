package com.opspulse.opspulse.services;
import com.opspulse.opspulse.dto.IncidentResponse;
import com.opspulse.opspulse.entity.Incident;
import com.opspulse.opspulse.entity.IncidentStatus;
import com.opspulse.opspulse.entity.Tenant;
import com.opspulse.opspulse.entity.User;
import com.opspulse.opspulse.repository.IncidentRepository;
import com.opspulse.opspulse.repository.TenantRepository;
import com.opspulse.opspulse.repository.UserRepository;
import com.opspulse.opspulse.service.IncidentService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})// boots the real Spring app for the test
public class IncidentServiceTenantIsolationTest {

    @Autowired
    IncidentService incidentService;
    @Autowired
    IncidentRepository incidentRepository;
    @Autowired
    TenantRepository tenantRepository;
    @Autowired
    UserRepository userRepository;

    private UUID tenantA;
    private UUID tenantB;
    private UUID incidentA;
    private UUID incidentB;

    @BeforeEach
    void setUp() {
        incidentRepository.deleteAll();
        userRepository.deleteAll();
        tenantRepository.deleteAll();

        // --- tenants ---
        Tenant a = new Tenant();
        a.setName("Tenant A");
        a = tenantRepository.save(a);

        Tenant b = new Tenant();
        b.setName("Tenant B");
        b = tenantRepository.save(b);

        tenantA = a.getId();
        tenantB = b.getId();

        User userA = new User();
        userA.setEmail("usera@tenanta.com");
        userA.setName("User A");
        userA.setPasswordHash("hash");
        userA.setRole("ADMIN");
        userA.setTenant(a);
        userA = userRepository.save(userA);

        User userB = new User();
        userB.setEmail("userb@tenantb.com");
        userB.setName("User B");
        userB.setPasswordHash("hash");
        userB.setRole("ADMIN");
        userB.setTenant(b);
        userB = userRepository.save(userB);

        // --- incidents ---
        Incident a1 = new Incident();
        a1.setTitle("A's incident");
        a1.setStatus(IncidentStatus.OPEN);
        a1.setSeverity("HIGH");
        a1.setTenant(a);
        a1.setCreated_By(userA);
        incidentA = incidentRepository.save(a1).getId();

        Incident b1 = new Incident();
        b1.setTitle("B's incident");
        b1.setStatus(IncidentStatus.OPEN);
        b1.setSeverity("HIGH");
        b1.setTenant(b);
        b1.setCreated_By(userB);
        incidentB = incidentRepository.save(b1).getId();
    }
    @Test
    @Transactional
    void listOpenIncidents_returnsOnlyOwnTenantsData(){
        List<IncidentResponse> result=incidentService.findByTenantIdAndStatus(tenantA,IncidentStatus.OPEN);

        assertThat(result).extracting(IncidentResponse::getId)
                .contains(incidentA)
                .doesNotContain(incidentB);
    }
    @Test
    void  updateStatus_cannotTouchAnotherIncident(){
        assertThatThrownBy(()->
                incidentService.updateStatus(incidentB,tenantA,IncidentStatus.RESOLVED))
                        .isInstanceOf(RuntimeException.class);
                Incident reload=incidentRepository.findById(incidentB).orElseThrow();
                assertThat(reload.getStatus()).isEqualTo(IncidentStatus.OPEN);

    }
    @Test

    void acknowledge_cannotTouchAnotherTenantsIncident() {
       assertThatThrownBy(()->
               incidentService.setacknowledgedAt(incidentB,tenantA))
                    .isInstanceOf(RuntimeException.class);
               Incident reloaded=incidentRepository.findById(incidentB).orElseThrow();
               assertThat(reloaded.getAcknowledgedAt()).isNull();
    }
    @Test
    @Transactional
    void updateStatus_happyPath_persistsForOwnTenant() {
        incidentService.updateStatus(incidentA,tenantA,IncidentStatus.RESOLVED);
        Incident reloaded=incidentRepository.findById(incidentA).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(IncidentStatus.RESOLVED);
    }
}
