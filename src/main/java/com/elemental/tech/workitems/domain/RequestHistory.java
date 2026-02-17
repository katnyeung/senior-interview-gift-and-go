package com.elemental.tech.workitems.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "request_history")
public class RequestHistory {

    @Id
    @Column(nullable = false, length = 36)
    private String requestId;

    @Column(nullable = false, length = 2000)
    private String requestUri;

    @Column(nullable = false)
    private Integer responseCode;

    @Column(length = 100)
    private String requestIp;

    @Column(length = 2000)
    private String requestIsp;

    @Column(length = 2)
    private String requestCountryCode;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private Long durationInMilli;

    protected RequestHistory() {}

    public RequestHistory(String requestUri, String requestIp, String requestIsp,
                          String requestCountryCode, Integer responseCode,
                          Instant timestamp, long durationInMilli) {
        this.requestId = UUID.randomUUID().toString();
        this.requestUri = requestUri;
        this.requestIp = requestIp;
        this.requestIsp = requestIsp;
        this.requestCountryCode = requestCountryCode;
        this.responseCode = responseCode;
        this.timestamp = timestamp;
        this.durationInMilli = durationInMilli;
    }
}