package com.dcloud.dependencies.mapper;

import com.dcloud.common.entity.log.SysLogBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SysLogMapper {
    void saveSysLogDetail(SysLogBean sysLogBean);

    SysLogBean getSysLogDetailByLogId(@Param("logId") String logId);

}
