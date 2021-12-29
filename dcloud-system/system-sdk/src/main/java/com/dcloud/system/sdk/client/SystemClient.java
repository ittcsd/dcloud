package com.dcloud.system.sdk.client;

import com.dcloud.system.sdk.hystrix.SystemClientHystrix;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "dcloud-system", fallback = SystemClientHystrix.class, path = "/dcloud-system-api/system")
public interface SystemClient {



}
