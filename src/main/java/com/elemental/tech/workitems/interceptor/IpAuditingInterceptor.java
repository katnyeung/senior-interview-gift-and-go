package com.elemental.tech.workitems.interceptor;

import com.elemental.tech.workitems.service.IpAuditingService;
import com.elemental.tech.workitems.service.dto.IpApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;

@Slf4j
@Component
public class IpAuditingInterceptor implements HandlerInterceptor {

    IpAuditingService ipAuditingService;

    public IpAuditingInterceptor(IpAuditingService ipAuditingService){
        this.ipAuditingService = ipAuditingService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        IpApiResponse ipApiResponse = ipAuditingService.retrieveIpInfo(request.getRemoteAddr());

        request.setAttribute("startTime", System.currentTimeMillis());
        request.setAttribute("requestTimestamp", Instant.now());
        request.setAttribute("ipInfo", ipApiResponse);

        log.debug("IP {} : {}", request.getRemoteAddr(), ipApiResponse);

        if (ipAuditingService.isIpBlocked(ipApiResponse)) {
            response.setStatus(403);
            response.getWriter().write(ipAuditingService.getReason(ipApiResponse));
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        IpApiResponse ipInfo = (IpApiResponse) request.getAttribute("ipInfo");

        ipAuditingService.record(
                request.getRequestURI(),
                request.getRemoteAddr(),
                ipInfo != null ? ipInfo.isp() : null,
                ipInfo != null ? ipInfo.countryCode() : null,
                response.getStatus(),
                (Instant) request.getAttribute("requestTimestamp"),
                duration
        );
    }

}
