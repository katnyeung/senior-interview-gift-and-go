package com.elemental.tech.workitems.api;

// TODO: 2. Left imports as a hint.


import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWireMock
@ActiveProfiles("test")
class IpAuditingIT {

    @Autowired
    MockMvc mvc;

    @InjectWireMock
    private WireMockServer wireMock;

    @Test
    void shouldReturn403ForBlockedCountry() throws Exception{
        wireMock.stubFor(WireMock.get(urlPathMatching("/json/.*"))
                .willReturn(okJson("""
                {
                    "status": "success",
                    "countryCode": "CN",
                    "country": "China",
                    "isp": "ISP",
                    "query": "1.2.3.4"
                }
                """)));

        mvc.perform(get("/api/work-items"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403ForBlockedISP() throws Exception{
        wireMock.stubFor(WireMock.get(urlPathMatching("/json/.*"))
                .willReturn(okJson("""
                {
                    "status": "success",
                    "countryCode": "GB",
                    "country": "United Kingdom",
                    "isp": "microsoft",
                    "query": "1.2.3.4"
                }
                """)));

        mvc.perform(get("/api/work-items"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotBlocked() throws Exception{
        wireMock.stubFor(WireMock.get(urlPathMatching("/json/.*"))
                .willReturn(okJson("""
                {
                    "status": "success",
                    "countryCode": "GB",
                    "country": "United Kingdom",
                    "isp": "Le Groupe Videotron Ltee",
                    "query": "1.2.3.4"
                }
                """)));

        mvc.perform(get("/api/work-items"))
                .andExpect(status().isOk());
    }

}
