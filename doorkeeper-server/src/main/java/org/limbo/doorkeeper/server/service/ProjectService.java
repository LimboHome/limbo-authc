/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.doorkeeper.server.service;

import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.ProjectAddParam;
import org.limbo.doorkeeper.api.model.param.ProjectQueryParam;
import org.limbo.doorkeeper.api.model.param.ProjectUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;

/**
 * @author Brozen
 * @date 2020/3/9 3:42 PM
 * @email brozen@qq.com
 */
public interface ProjectService {

    /**
     * 添加项目
     * @param param
     * @param isActivated
     * @return
     */
    ProjectVO addProject(ProjectAddParam param, Boolean isActivated);

    /**
     * 更新项目
     * @param param
     * @return
     */
    Integer updateProject(ProjectUpdateParam param);

    /**
     * 删除项目 假删除
     */
    ProjectVO deleteProject(Long projectId);

    /**
     * 根据id 获取项目 不会返回secret
     */
    ProjectVO get(Long projectId);

    /**
     * 获取秘钥
     */
    String getSecret(Long projectId);

    /**
     * 分页查询项目列表
     */
    Page<ProjectVO> queryProjectPage(ProjectQueryParam param);
}
