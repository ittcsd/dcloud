package com.dcloud.common.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/*
* 用户信息
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -8641051415734363294L;
    private Long id;
    private String userId;
    private String employeeNum;
    private String headImageUrl;
    private String realName;
    private String nickName;
    private String userName;
    private String password;
    private String phone;
    private String email;
    private Integer sex;
    private String age;
    private Date birthday;
    private String address;
    private String note;
    private Integer status;
    private String companyId;
    private String deptId;
    private String creator;
    private String updater;
    private Date createTime;
    private Date updateTime;
}
