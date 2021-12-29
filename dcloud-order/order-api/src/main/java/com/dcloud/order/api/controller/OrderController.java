package com.dcloud.order.api.controller;

import com.dcloud.common.entity.response.DcloudJsonResult;
import com.dcloud.user.sdk.client.UserClient;
import com.dcloud.user.entity.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@Api(value = "订单管理接口", tags = "订单管理接口：提供页面增，删，改，查")
@RequestMapping("/order")
@RefreshScope
public class OrderController {

    @Resource
    private UserClient userClient;

    @GetMapping("/getOrderByUserId/{userId}")
    @ApiOperation("查询用户订单信息")
    DcloudJsonResult<UserInfo> getUserInfoById(@PathVariable("userId") String userId){
        return userClient.getUserInfoById(userId);
    }


}
