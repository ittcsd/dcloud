package com.dcloud.upload.sdk.client;

import com.dcloud.upload.sdk.hystrix.UploadClientHystrix;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "dcloud-upload", fallback = UploadClientHystrix.class, path = "/dcloud-upload-api/upload")
public interface UploadClient {



}
