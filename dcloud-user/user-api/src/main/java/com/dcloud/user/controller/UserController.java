package com.dcloud.user.controller;

import com.dcloud.common.entity.annotation.SysLog;
import com.dcloud.common.entity.log.SysLogTypeEnum;
import com.dcloud.common.entity.response.DcloudJsonResult;
import com.dcloud.dependencies.utlils.DcloudValidUtil;
import com.dcloud.user.entity.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dcloud.user.service.UserService;
import javax.annotation.Resource;
/**
 * @author: dcloud
 * @Email: ittcsd@163.com
 * @Date: 2021/12/28 11:29
 * @Description: 用户管理
 **/
@Slf4j
@RestController
@Api(value = "用户管理接口", tags = "用户管理接口：提供页面增，删，改，查")
@RequestMapping("/user")
@RefreshScope
public class UserController {

    @Resource
    private UserService userService;

    @SysLog(type = SysLogTypeEnum.GET, note = "查询用户信息", model = "用户模块", saveRequestData = true)
    @ApiOperation("查询用户信息")
    @GetMapping("/getUserInfoById/{userId}")
    public DcloudJsonResult<UserInfo> getUserInfoById(@PathVariable("userId") String userId) {
        DcloudValidUtil.checkParams(userId);
        return userService.getUserInfoById(userId);
    }

}
