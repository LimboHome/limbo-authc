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

package org.limbo.doorkeeper.api.model.param.update;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.Logic;
import org.limbo.doorkeeper.api.model.param.add.PolicyGroupAddParam;
import org.limbo.doorkeeper.api.model.param.add.PolicyParamAddParam;
import org.limbo.doorkeeper.api.model.param.add.PolicyRoleAddParam;
import org.limbo.doorkeeper.api.model.param.add.PolicyUserAddParam;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/6 7:59 下午
 */
@Data
public class PolicyUpdateParam {

    @Schema(description ="名称")
    private String name;

    @Schema(description ="描述")
    private String description;

    @Schema(description = "判断逻辑，只有组合策略需要")
    private Logic logic;

    @Schema(description ="执行逻辑")
    private Intention intention;

    @Schema(description ="是否启用")
    private Boolean isEnabled;

    @Schema(description ="操作策略")
    private List<PolicyParamAddParam> params;

    @Schema(description ="角色策略")
    private List<PolicyRoleAddParam>  roles;

    @Schema(description ="用户策略")
    private List<PolicyUserAddParam>  users;

    @Schema(description ="用户组策略")
    private List<PolicyGroupAddParam> groups;

}
