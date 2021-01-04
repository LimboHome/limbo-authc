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

package org.limbo.doorkeeper.server.service.impl;

import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.AdminRealmQueryParam;
import org.limbo.doorkeeper.api.model.vo.AdminRealmVO;
import org.limbo.doorkeeper.server.dao.AdminRealmMapper;
import org.limbo.doorkeeper.server.service.AdminRealmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Devil
 * @date 2021/1/4 10:59 上午
 */
@Service
public class AdminRealmServiceImpl implements AdminRealmService {

    @Autowired
    private AdminRealmMapper adminRealmMapper;

    @Override
    public Page<AdminRealmVO> page(AdminRealmQueryParam param) {
        long count = adminRealmMapper.pageVOCount(param);
        param.setTotal(count);
        if (count > 0) {
            param.setData(adminRealmMapper.pageVOS(param));
        }
        return param;
    }
}
