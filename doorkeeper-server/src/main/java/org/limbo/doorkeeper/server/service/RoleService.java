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

package org.limbo.doorkeeper.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.param.role.RoleAddParam;
import org.limbo.doorkeeper.api.model.param.role.RoleQueryParam;
import org.limbo.doorkeeper.api.model.param.role.RoleUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleVO;
import org.limbo.doorkeeper.server.dao.RoleMapper;
import org.limbo.doorkeeper.server.entity.Role;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/4 5:29 下午
 */
@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Transactional
    public RoleVO add(RoleAddParam param) {
        // todo 是否能操作这个realm 这个client

        Role role = EnhancedBeanUtils.createAndCopy(param, Role.class);
        try {
            roleMapper.insert(role);
        } catch (DuplicateKeyException e) {
            throw new ParamException("角色已存在");
        }
        return EnhancedBeanUtils.createAndCopy(role, RoleVO.class);
    }

    public List<RoleVO> list(RoleQueryParam param) {
        List<Role> roles = roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .eq(Role::getRealmId, param.getRealmId())
                .eq(param.getIsEnabled() != null, Role::getIsEnabled, param.getIsEnabled())
                .eq(param.getIsDefault() != null, Role::getIsDefault, param.getIsDefault())
                .like(StringUtils.isNotBlank(param.getName()), Role::getName, param.getName())
                .eq(param.getClientId() != null, Role::getClientId, param.getClientId())
                .orderByDesc(Role::getRoleId)
        );
        return EnhancedBeanUtils.createAndCopyList(roles, RoleVO.class);
    }

    public RoleVO get(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        return EnhancedBeanUtils.createAndCopy(role, RoleVO.class);
    }

    public void update(Long roleId, RoleUpdateParam param) {
        // todo 能否操作
        roleMapper.update(null, Wrappers.<Role>lambdaUpdate()
                .set(Role::getDescription, param.getDescription())
                .set(Role::getIsDefault, param.getIsDefault())
                .set(Role::getIsEnabled, param.getIsEnabled())
                .eq(Role::getRoleId, roleId)
        );
    }

}
