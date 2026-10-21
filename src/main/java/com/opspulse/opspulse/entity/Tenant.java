package com.opspulse.opspulse.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name="tenant")
@Getter
@Setter
public class Tenant {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, updatable=false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt=Instant.now();
    }

}
