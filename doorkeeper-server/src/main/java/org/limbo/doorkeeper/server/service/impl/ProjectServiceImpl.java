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

package org.limbo.doorkeeper.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.ProjectAddParam;
import org.limbo.doorkeeper.api.model.param.ProjectQueryParam;
import org.limbo.doorkeeper.api.model.param.ProjectUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;
import org.limbo.doorkeeper.server.dao.ProjectMapper;
import org.limbo.doorkeeper.server.entity.Project;
import org.limbo.doorkeeper.server.service.ProjectService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.UUIDUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Brozen
 * @date 2020/3/9 3:42 PM
 * @email brozen@qq.com
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    @Transactional
    public ProjectVO addProject(ProjectAddParam param, Boolean isActivated) {
        Project project = EnhancedBeanUtils.createAndCopy(param, Project.class);
        project.setIsActivated(isActivated);
        if (StringUtils.isBlank(param.getProjectSecret())) {
            project.setProjectSecret(UUIDUtils.get());
        }
        try {
            projectMapper.insert(project);
        } catch (DuplicateKeyException e) {
            throw new ParamException("项目已存在");
        }

        return EnhancedBeanUtils.createAndCopy(project, ProjectVO.class);
    }

    @Override
    @Transactional
    public Integer updateProject(ProjectUpdateParam param) {
        Project project = projectMapper.selectById(param.getProjectId());
        Verifies.notNull(project, "项目不存在");

        LambdaUpdateWrapper<Project> update = Wrappers.<Project>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getProjectName()), Project::getProjectName, param.getProjectName())
                .set(StringUtils.isNotBlank(param.getProjectSecret()), Project::getProjectSecret, param.getProjectSecret())
                .set(StringUtils.isNotBlank(param.getProjectDescribe()), Project::getProjectDescribe, param.getProjectDescribe())
                .eq(Project::getProjectId, param.getProjectId());

        return projectMapper.update(null, update);
    }

    @Override
    @Transactional
    public ProjectVO deleteProject(Long projectId) {
        ProjectVO project = get(projectId);
        projectMapper.update(null, Wrappers.<Project>lambdaUpdate()
                .set(Project::getIsDeleted, true)
                .eq(Project::getProjectId, projectId)
        );
        return project;
    }

    @Override
    public ProjectVO get(Long projectId) {
        Project project = projectMapper.selectOne(Wrappers.<Project>lambdaQuery()
                .eq(Project::getProjectId, projectId)
                .eq(Project::getIsDeleted, false)
                .eq(Project::getIsActivated, true)
        );
        return EnhancedBeanUtils.createAndCopy(project, ProjectVO.class);
    }

    @Override
    public String getSecret(Long projectId) {
        Project project = projectMapper.selectOne(Wrappers.<Project>lambdaQuery()
                .select(Project::getProjectSecret)
                .eq(Project::getProjectId, projectId)
        );
        return project.getProjectSecret();
    }

    @Override
    public Page<ProjectVO> queryProjectPage(ProjectQueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Project> mpage = MyBatisPlusUtils.pageOf(param);
        LambdaQueryWrapper<Project> condition = Wrappers.<Project>lambdaQuery()
                .like(StringUtils.isNotBlank(param.getProjectName()), Project::getProjectName, param.getProjectName())
                .eq(Project::getIsDeleted, false);
        mpage = projectMapper.selectPage(mpage, condition);

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), ProjectVO.class));
        return param;
    }

}