package com.dexinsights.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/v1/test")
    public String test() {
        return "Dex Insights backend is running";
    }
}