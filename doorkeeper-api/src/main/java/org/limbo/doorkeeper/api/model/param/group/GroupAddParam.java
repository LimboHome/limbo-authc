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

package org.limbo.doorkeeper.api.model.param.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Devil
 * @date 2021/1/9 10:57 上午
 */
@Data
public class GroupAddParam {

    @NotNull(message = "名称不能为空")
    @Schema(title = "名称", required = true)
    private String name;

    @Schema(title = "描述")
    private String description;

    @Schema(title = "父ID")
    private Long parentId;

    @Schema(title = "默认添加")
    private Boolean isDefault;

}
