package com.backbone.core.demo.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InstanceService {

//  Read from .properties or environment value!
//
//  ${property.name in application.yaml}, or
//  ${ENV_VALUE:default_value}
    @Value("${HOSTNAME:LOCAL}")
    private String hostName;

    public String getHostName() {
        return hostName;
    }
}
