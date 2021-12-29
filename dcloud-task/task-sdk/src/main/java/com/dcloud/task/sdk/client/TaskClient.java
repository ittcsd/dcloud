package com.dcloud.task.sdk.client;

import com.dcloud.task.sdk.hystrix.TaskClientHystrix;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "dcloud-task", fallback = TaskClientHystrix.class, path = "/dcloud-task-api/task")
public interface TaskClient {



}
