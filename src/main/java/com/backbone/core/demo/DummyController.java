package com.backbone.core.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DummyController {
    @Autowired
    InstanceService service;

    /*
     * L1 REST is like SOAP style method names
     * L2 REST separate GET/POST/DELETE/PUT requests @GetMapping ..
     * L3 REST HATEOS (return also next actions
     */
    @RequestMapping("/dummy")
    public String dummy()  {
        return "Hello , " + service.getHostName();
    }

    @GetMapping({"/dummy/{name}", "/dummy-service/{name}"})
    public String dummy2(@PathVariable String name)  {
        return "Hello , " + name;
    }
}
