package com.goodsflow.admin.home.controller;

import com.goodsflow.common.base.ResData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health")
    public ResData<String> health() {
        return ResData.success("goodsFlow admin service is running");
    }
}
