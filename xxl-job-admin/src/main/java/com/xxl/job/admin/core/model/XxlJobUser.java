package com.xxl.job.admin.core.model;

import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author xuxueli 2019-05-04 16:43:12
 */
public class XxlJobUser {

    private int id;
    private String username;        // 账号
    private String password;        // 密码
    private int role;                // 角色：0-普通用户、1-管理员
    private String permission;    // 权限：执行器ID列表，多个逗号分割
    private Integer type;           //类型，0：系统初始化用户，1：其他正常用户；-1：已删除；

    private Date createdTime;
    private Date modifiedTime;

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    // plugin
    public boolean validPermission(int jobGroup) {
        if (this.role == 1) {
            return true;
        } else {
            if (StringUtils.hasText(this.permission)) {
                for (String permissionItem : this.permission.split(",")) {
                    if (String.valueOf(jobGroup).equals(permissionItem)) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
