package com.dcloud.gateway.controller;

import com.dcloud.common.entity.response.DcloudJsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/fallbackA")
    public DcloudJsonResult<?> fallbackA() {
        return DcloudJsonResult.fail("服务不可用");
    }
}