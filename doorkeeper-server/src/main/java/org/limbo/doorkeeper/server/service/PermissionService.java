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

package org.limbo.doorkeeper.server.service;

import org.limbo.doorkeeper.api.model.param.PermissionAddParam;
import org.limbo.doorkeeper.api.model.param.PermissionBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.PermissionUpdateParam;
import org.limbo.doorkeeper.server.entity.Permission;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/19 8:31 PM
 */
public interface PermissionService {
    /**
     * 新增权限
     */
    Permission addPermission(PermissionAddParam param);

    /**
     * 修改权限
     */
    int updatePermission(PermissionUpdateParam param);

    /**
     * 删除权限
     */
    void deletePermission(List<Long> permissionIds);

    /**
     * 批量修改权限
     */
    void batchUpdate(PermissionBatchUpdateParam param);

}
