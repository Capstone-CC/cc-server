package com.cau.cc.cors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/api/**"}, description = "API 필터")
@Component
@Slf4j
public class CORSFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        httpServletRequest.setCharacterEncoding("utf-8");
        //set header

        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods",
                "POST, GET, DELETE, PUT, PATCH, OPTIONS");
        httpServletResponse.setHeader("Accept-Charset", "utf-8");
        httpServletResponse.setHeader("Cache-Control", "no-cache");
        httpServletResponse.addHeader("Content-Type",MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setHeader("Expires", "-1");
        httpServletResponse.setHeader("Pragma", "no-cache");

        chain.doFilter(httpServletRequest, httpServletResponse);
    }

    @Override
    public void destroy() {

    }
}
