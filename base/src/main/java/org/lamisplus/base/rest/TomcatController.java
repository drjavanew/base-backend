package org.lamisplus.base.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TomcatController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello LamisPlus from Tomcat ";
    }
}