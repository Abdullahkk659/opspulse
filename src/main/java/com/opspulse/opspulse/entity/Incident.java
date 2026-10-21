package com.opspulse.opspulse.entity;
import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="incidents")
@Getter
@Setter

public class Incident {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    @Column(nullable = false)
    private String severity;

    @ManyToOne(fetch=FetchType.LAZY,optional = false)
    @JoinColumn(name="tenant_id",nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch =FetchType.LAZY,optional=false)
    @JoinColumn(name="created_by",nullable = false)
    private User created_By;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private int escalationLevel=0;
    private Instant acknowledgedAt;

    @PrePersist
    protected void onCreate(){
        Instant now=Instant.now();
        this.createdAt=now;
        this.updatedAt=now;
        if(this.status==null){
            this.status=IncidentStatus.OPEN;
        }
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt=Instant.now();
    }

}
