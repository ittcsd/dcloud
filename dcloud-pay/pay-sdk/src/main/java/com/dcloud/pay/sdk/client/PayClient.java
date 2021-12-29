package com.dcloud.pay.sdk.client;

import com.dcloud.pay.sdk.hystrix.PayClientHystrix;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "dcloud-pay", fallback = PayClientHystrix.class, path = "/dcloud-pay-api/pay")
public interface PayClient {



}
