package com.elemental.tech.workitems.service.dto;

public record IpApiResponse(
        String status,
        String country,
        String countryCode,
        String isp,
        String query
) {}