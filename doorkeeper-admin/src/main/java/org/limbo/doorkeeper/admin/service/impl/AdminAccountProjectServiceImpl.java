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

package org.limbo.doorkeeper.admin.service.impl;

import org.limbo.doorkeeper.admin.dao.AdminAccountProjectMapper;
import org.limbo.doorkeeper.admin.entity.AdminAccountProject;
import org.limbo.doorkeeper.admin.service.AdminAccountProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Devil
 * @date 2020/11/24 10:25 AM
 */
@Service
public class AdminAccountProjectServiceImpl implements AdminAccountProjectService {

    @Autowired
    private AdminAccountProjectMapper adminAccountProjectMapper;

    @Override
    public List<AdminAccountProject> getByAccount(Long accountId) {
        return adminAccountProjectMapper.getByAccount(accountId);
    }

    @Override
    public AdminAccountProject getByAccountProject(Long accountId, Long projectId) {
        return adminAccountProjectMapper.getByAccountProject(accountId, projectId);
    }
}