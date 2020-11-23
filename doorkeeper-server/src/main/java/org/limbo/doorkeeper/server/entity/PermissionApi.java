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
import org.limbo.doorkeeper.api.constants.PermissionPolicy;

import java.util.Date;

/**
 *
 * 权限和api绑定关系
 *
 * @author Devil
 * @date 2020/11/18 7:22 PM
 */
@Data
@TableName("l_permission_api")
public class PermissionApi {

    @TableId(type = IdType.AUTO)
    private Long permissionApiId;

    private Long permissionId;

    private Long apiId;

    /**
     * @see PermissionPolicy
     */
    private String policy;

}