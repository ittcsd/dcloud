package com.dcloud.user.controller;

import com.dcloud.common.entity.annotation.SysLog;
import com.dcloud.common.entity.log.SysLogTypeEnum;
import com.dcloud.common.entity.response.DcloudJsonResult;
import com.dcloud.dependencies.utlils.DcloudValidUtil;
import com.dcloud.user.entity.UserInfo;
import com.dcloud.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: dcloud
 * @Email: ittcsd@163.com
 * @Date: 2021/12/28 11:29
 * @Description: 用户信息
 **/
@Slf4j
@RestController
@Api(value = "用户信息管理接口", tags = "用户信息管理接口：提供页面增，删，改，查")
@RequestMapping("/user")
@RefreshScope
public class UserInfoController {

    @Resource
    private UserService userService;

    @ApiOperation("测试获取用户配置")
    @GetMapping("/getUserInfoByNacosConfig")
    public DcloudJsonResult<UserInfo> getUserInfoByNacosConfig() {
        return userService.getUserInfoByNacosConfig();
    }

}
