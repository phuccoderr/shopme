package com.shopme.setting;

import com.shopme.common.entity.setting.Setting;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Order(-123)
public class SettingFilter implements Filter {
    @Autowired private SettingService service;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String url = servletRequest.getRequestURL().toString();
        if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png")) {
            filterChain.doFilter(request,response);
            return;
        }

        List<Setting> settings = service.getGeneralSettings();
        for (Setting setting : settings) {
            request.setAttribute(setting.getKey(),setting.getValue());
        }
        filterChain.doFilter(request,response);
    }
}
