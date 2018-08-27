package com.albedo.java.web.filter;

import com.albedo.java.common.config.AlbedoProperties;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BodyFilter extends OncePerRequestFilter {
    private final AlbedoProperties albedoProperties;

    public BodyFilter(AlbedoProperties albedoProperties) {
        this.albedoProperties = albedoProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().contains("/file/upload")){
            filterChain.doFilter(request, response);
        }else{
            filterChain.doFilter(new CustomHttpServletRequestWrapper(request), response);
        }
    }
}
