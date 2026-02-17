package com.elemental.tech.workitems.service;

import com.elemental.tech.workitems.domain.RequestHistory;
import com.elemental.tech.workitems.persistence.RequestHistoryRepository;
import com.elemental.tech.workitems.service.dto.IpApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class IpAuditingService {
    private static final Set<String> BLOCKED_COUNTRY = Set.of("CN","ES","US");
    private static final List<String> BLOCKED_ISP = List.of(
            "amazon", "aws",
            "google cloud","google llc","gcp",
            "microsoft", "azure");

    private final RestClient restClient;

    RequestHistoryRepository requestHistoryRepository;

    public IpAuditingService(RequestHistoryRepository requestHistoryRepository, RestClient restClient) {
        this.restClient = restClient;
        this.requestHistoryRepository = requestHistoryRepository;
    }

    public IpApiResponse retrieveIpInfo(String ip) {
        String url = "/json/" + ip;
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(IpApiResponse.class);
    }

    public boolean isIpBlocked(IpApiResponse ip) {
        // ip-api fallback if request not success
        if (!ip.status().equals("success")) {
            return false;
        }

        // country check
        if(BLOCKED_COUNTRY.contains(ip.countryCode()))
            return true;

        if (ip.isp() == null) return false;

        // isp check
        String isp = ip.isp().toLowerCase();
        for (String keyword : BLOCKED_ISP) {
            if (isp.contains(keyword)) return true;
        }

        return false;
    }

    public String getReason(IpApiResponse ip) {
        if (BLOCKED_COUNTRY.contains(ip.countryCode()))
            return "Blocked country: " + ip.countryCode();

        if (ip.isp() != null) {
            String isp = ip.isp().toLowerCase();
            for (String keyword : BLOCKED_ISP) {
                if (isp.contains(keyword))
                    return "Blocked ISP: " + ip.isp();
            }
        }

        return "Unknown Reason : " + ip;
    }

    public void record(String requestURI,
                       String remoteAddr,
                       String isp,
                       String countryCode,
                       int status,
                       Instant requestTimestamp,
                       long duration) {

        RequestHistory rh = new RequestHistory(requestURI , remoteAddr, isp, countryCode, status, requestTimestamp, duration);

        requestHistoryRepository.save(rh);
    }
}