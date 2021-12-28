package com.dcloud.sdk.client;

import com.dcloud.common.entity.response.DcloudJsonResult;
import com.dcloud.sdk.hystrix.UserClientHystrix;
import com.dcloud.user.entity.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "dcloud-user", fallback = UserClientHystrix.class, path = "/dcloud-user-api/user")
public interface UserClient {

    @GetMapping("/getUserInfoById/{userId}")
    DcloudJsonResult<UserInfo> getUserInfoById(@PathVariable("userId") String userId);

}
