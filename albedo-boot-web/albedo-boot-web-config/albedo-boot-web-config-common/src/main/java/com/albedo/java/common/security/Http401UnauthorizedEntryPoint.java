package com.albedo.java.common.security;

import com.albedo.java.common.config.AlbedoProperties;
import com.albedo.java.util.domain.CustomMessage;
import com.albedo.java.web.rest.base.BaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Returns a 401 error code (Unauthorized) to the client.
 */
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    private final Logger log = LoggerFactory.getLogger(Http401UnauthorizedEntryPoint.class);

    private final AlbedoProperties albedoProperties;

    public Http401UnauthorizedEntryPoint(AlbedoProperties albedoProperties) {
        this.albedoProperties = albedoProperties;
    }

    /**
     * Always returns a 401 error code to the client.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2){

        log.debug("Pre-authenticated entry point called. Rejecting access");

        response.setStatus(HttpStatus.FORBIDDEN.value());
        BaseResource.writeJsonHttpResponse(CustomMessage.createError("权限不足或登录超时").setCode(HttpStatus.FORBIDDEN), response);

    }
}
