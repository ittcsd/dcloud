package com.dcloud.user.sdk.hystrix;

import com.dcloud.common.entity.response.DcloudJsonResult;
import com.dcloud.user.sdk.client.UserClient;
import com.dcloud.user.entity.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserClientHystrix implements UserClient {
    public DcloudJsonResult<UserInfo> getUserInfoById(String userId) {
        return DcloudJsonResult.fail("UserClientHystrix fallback: getUserInfoById 服务调用失败");
    }
}
