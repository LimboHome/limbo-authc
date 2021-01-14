/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.server.controller.realm;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.resource.RealmAddParam;
import org.limbo.doorkeeper.api.model.vo.RealmVO;
import org.limbo.doorkeeper.server.controller.BaseController;
import org.limbo.doorkeeper.server.service.RealmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/3 5:41 下午
 */
@Slf4j
@RestController
@RequestMapping("/admin/realm")
public class AdminRealmController extends BaseController {

    @Autowired
    private RealmService realmService;

    @Operation(summary = "新建域")
    @PostMapping
    public Response<RealmVO> add(@RequestBody @Validated RealmAddParam param) {
        return Response.success(realmService.add(getUserId(), param));
    }

    @Operation(summary = "查询账户拥有的域")
    @GetMapping
    public Response<List<RealmVO>> userRealms() {
        return Response.success(realmService.userRealms(getUserId()));
    }

}