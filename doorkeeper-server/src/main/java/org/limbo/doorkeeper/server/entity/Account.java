/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.limbo.doorkeeper.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Devil
 * @date 2020/11/18 7:03 PM
 */
@Data
@TableName("l_account")
public class Account {

    @TableId(type = IdType.AUTO)
    private Long accountId;

    private Long projectId;
    /**
     * 账户名称
     */
    private String username;
    /**
     * 账户描述
     */
    private String accountDescribe;
    /**
     * 是否超级管理员
     */
    private Boolean isSuperAdmin;
    /**
     * 是否激活  未激活的账户没有权限
     */
    private Boolean isActivated;

    private Date gmtCreated;

    private Date gmtModified;
}