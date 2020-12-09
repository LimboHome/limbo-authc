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
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.param.ProjectAccountQueryParam;
import org.limbo.doorkeeper.api.model.param.RepasswordParam;
import org.limbo.doorkeeper.api.model.vo.AccountGrantVO;
import org.limbo.doorkeeper.api.model.vo.ProjectAccountVO;
import org.limbo.doorkeeper.api.model.vo.SessionAccount;
import org.limbo.doorkeeper.api.model.vo.SessionVO;
import org.limbo.doorkeeper.server.service.AccountService;
import org.limbo.doorkeeper.server.service.AuthenticationService;
import org.limbo.doorkeeper.server.service.ProjectAccountService;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author devil
 * @date 2020/3/11
 */
@Slf4j
@Tag(name = "会话")
@RestController
@RequestMapping("/session")
public class SessionController extends BaseController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ProjectAccountService projectAccountService;

    @Autowired
    private AccountService accountService;

    @GetMapping
    @Operation(summary = "获取会话")
    public Response<SessionVO> session() {
        SessionVO session = getSession();
        return Response.ok(session);
    }

    /**
     * 切换用户当前选择的项目
     */
    @PutMapping("/project/{projectId}")
    @Operation(summary = "切换用户当前选择的项目")
    public Response<SessionVO> switchProject(@PathVariable("projectId") @Valid @NotNull(message = "项目不存在") Long projectId) {
        SessionVO session = getSession();
        SessionAccount account = session.getAccount();

        ProjectAccountQueryParam queryParam = new ProjectAccountQueryParam();
        queryParam.setProjectId(projectId);
        queryParam.setAccountId(account.getAccountId());
        List<ProjectAccountVO> list = projectAccountService.list(queryParam);
        Verifies.notEmpty(list, "无权操作此项目！");

        account.setCurrentProject(list.get(0));
        sessionDAO.save(session);
        return Response.ok(getSession());
    }

    @Operation(summary = "获取当前项目页面权限信息")
    @GetMapping("/grant-info")
    public Response<AccountGrantVO> getGrantInfo() {
        // 拿到用户管理端权限
        return Response.ok(authenticationService.getGrantedAdminPermissions(getCurrentProjectId(), getAccountId()));
    }

    @Operation(summary = "会话项目列表")
    @GetMapping("/project")
    public Response<List<ProjectAccountVO>> project() {
        ProjectAccountQueryParam param = new ProjectAccountQueryParam();
        param.setAccountId(getAccountId());
        return Response.ok(projectAccountService.list(param));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/repassword")
    public Response<Boolean> repassword(@Validated @RequestBody RepasswordParam param) {
        accountService.repassword(getAccountId(), param);
        return Response.ok(true);
    }

    @Operation(summary = "注销账户")
    @GetMapping("/logout")
    public Response<Boolean> logout() {
        SessionVO session = getSession();
        sessionDAO.destroySession(session.getToken());
        return Response.ok(true);
    }

}
