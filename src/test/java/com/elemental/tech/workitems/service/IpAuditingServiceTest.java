package com.elemental.tech.workitems.service;

import com.elemental.tech.workitems.service.dto.IpApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class IpAuditingServiceTest {
/*
Validation rules:
    1. You must block IPs from the following countries
        a. China CN
        b. Spain ES
        c. USA US
    2. You must block IPs that are from the following ISPs/Data Centres
        a. AWS
        b. GCP
        c. Azure
 */

    @InjectMocks
    private IpAuditingService ipAuditingService;

    @Test
    void shouldNotBlocked(){
        var response = new IpApiResponse("success", "United Kingdom", "GB", "Le Groupe Videotron Ltee", "1.2.3.4");
        assertThat(ipAuditingService.isIpBlocked(response)).isFalse();
    }

    @Test
    void shouldChinaBlocked(){
        var response = new IpApiResponse("success", "China", "CN", "Le Groupe Videotron Ltee", "1.2.3.4");
        assertThat(ipAuditingService.isIpBlocked(response)).isTrue();
    }

    @Test
    void shouldSpainBlocked(){
        var response = new IpApiResponse("success", "Spain", "ES", "Le Groupe Videotron Ltee", "1.2.3.4");
        assertThat(ipAuditingService.isIpBlocked(response)).isTrue();
    }

    @Test
    void shouldUSABlocked(){
        var response = new IpApiResponse("success", "USA", "US", "Le Groupe Videotron Ltee", "1.2.3.4");
        assertThat(ipAuditingService.isIpBlocked(response)).isTrue();
    }

    @Test
    void shouldAWSBlocked(){
        var response = new IpApiResponse("success", "United Kingdom", "GB", "Amazon Technologies Inc.", "1.2.3.4");
        assertThat(ipAuditingService.isIpBlocked(response)).isTrue();
    }

    @Test
    void shouldGCPBlocked(){
        var response = new IpApiResponse("success", "United Kingdom", "GB", "Google Cloud", "1.2.3.4");
        assertThat(ipAuditingService.isIpBlocked(response)).isTrue();
    }

    @Test
    void shouldAzureBlocked(){
        var response = new IpApiResponse("success", "United Kingdom", "GB", "Microsoft Corporation", "1.2.3.4");
        assertThat(ipAuditingService.isIpBlocked(response)).isTrue();
    }

    @Test
    void shouldBlockedWithReason(){
        var response = new IpApiResponse("success", "United Kingdom", "GB", "Microsoft Corporation", "1.2.3.4");
        assertThat(ipAuditingService.isIpBlocked(response)).isTrue();
        assertThat(ipAuditingService.getReason(response)).contains("Blocked ISP:");
    }

}
