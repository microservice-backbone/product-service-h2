package com.backbone.core.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InstanceService {

//  ${ENV_VALUE:default_value}
    @Value("${HOSTNAME:LOCAL}")
    private String hostName;

    public String getHostName() {
        return hostName;
    }
}
