package com.dcloud.user.service.impl;

import com.dcloud.common.entity.response.DcloudJsonResult;
import com.dcloud.user.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.dcloud.user.service.UserService;

import java.util.Date;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public DcloudJsonResult<UserInfo> getUserInfoById(String userId) {
        UserInfo userInfo = new UserInfo();
        userInfo.setAddress("武汉市");
        userInfo.setAge("10");
        userInfo.setBirthday(new Date());
        userInfo.setEmail("ittcsd@163.com");
        userInfo.setId(1L);
        userInfo.setNickName("爱神丘比特");
        return DcloudJsonResult.success(userInfo);
    }
}
