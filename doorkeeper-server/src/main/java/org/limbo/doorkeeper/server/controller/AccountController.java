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

package org.limbo.doorkeeper.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.AccountAddParam;
import org.limbo.doorkeeper.api.model.param.AccountBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.AccountQueryParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.server.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Devil
 * @date 2020/11/20 3:32 PM
 */
@Tag(name = "账户")
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    @Operation(summary = "新增账户")
    public Response<AccountVO> add(@RequestBody AccountAddParam param) {
        return Response.ok(accountService.addAccount(param));
    }

    @PutMapping
    @Operation(summary = "修改账户")
    public Response<Integer> update(@RequestBody AccountBatchUpdateParam param) {
        return Response.ok(accountService.updateAccount(param));
    }

    @GetMapping
    @Operation(summary = "查询账户列表")
    public Response<Page<AccountVO>> page(AccountQueryParam param) {
        return Response.ok(accountService.queryPage(param));
    }

}
