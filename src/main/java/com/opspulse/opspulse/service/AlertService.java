package com.opspulse.opspulse.service;

import com.opspulse.opspulse.entity.Incident;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class AlertService {

    @Value("${alert.webhook.url}")
    private String webhookUrl;

    private final RestClient restClient = RestClient.create();

    public void sendEscalationAlert(Incident incident) {
        String message = "🚨 Incident escalated: " + incident.getTitle()
                + " (severity " + incident.getSeverity() + ")"
                + " for " + incident.getTenant().getName();

        try {
            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("text", message))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.out.println("Failed to send alert: " + e.getMessage());
        }
    }
}