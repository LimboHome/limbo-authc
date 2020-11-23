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

package org.limbo.doorkeeper.admin.controller;

import org.limbo.doorkeeper.api.client.RoleClient;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.RoleQueryParam;
import org.limbo.doorkeeper.api.model.param.RoleUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Devil
 * @date 2020/11/20 4:25 PM
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleClient roleClient;

    @PostMapping
    public Response<RoleVO> add(@RequestBody RoleAddParam param) {
        return roleClient.add(param);
    }

    @PutMapping("/{roleId}")
    public Response<Integer> update(@PathVariable("roleId") Long roleId, @RequestBody RoleUpdateParam param) {
        return roleClient.update(roleId, param);
    }

    @DeleteMapping
    public Response<Integer> delete(@NotEmpty(message = "角色不存在") List<Long> roleIds) {
        return roleClient.delete(roleIds);
    }


    @GetMapping
    public Response<Page<RoleVO>> page(RoleQueryParam param) {
        return roleClient.page(param);
    }

}
