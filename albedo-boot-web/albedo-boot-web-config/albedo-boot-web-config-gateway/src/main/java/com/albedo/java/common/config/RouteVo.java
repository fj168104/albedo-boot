package com.albedo.java.common.config;


import lombok.Data;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * View Model that stores a route managed by the Gateway.
 */
@Data
public class RouteVo {

    private String path;

    private String serviceId;

    private List<ServiceInstance> serviceInstances;
}
