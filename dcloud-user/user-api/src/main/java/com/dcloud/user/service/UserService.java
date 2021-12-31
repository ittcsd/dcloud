package com.dcloud.user.service;

import com.dcloud.common.entity.response.DcloudJsonResult;
import com.dcloud.user.entity.UserInfo;

public interface UserService {
    DcloudJsonResult<UserInfo> getUserInfoById(String userId);

    DcloudJsonResult<UserInfo> getUserInfoByNacosConfig();

}
