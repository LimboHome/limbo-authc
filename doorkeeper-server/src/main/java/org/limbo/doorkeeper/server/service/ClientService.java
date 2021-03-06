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
import org.limbo.doorkeeper.api.model.param.update.ClientUpdateParam;
import org.limbo.doorkeeper.api.model.vo.ClientVO;
import org.limbo.doorkeeper.server.infrastructure.po.ClientPO;
import org.limbo.doorkeeper.server.infrastructure.mapper.ClientMapper;
import org.limbo.doorkeeper.server.infrastructure.exception.ParamException;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Devil
 * @date 2021/1/4 2:59 下午
 */
@Service
public class ClientService {

    @Autowired
    private ClientMapper clientMapper;

    public ClientVO get(Long realmId, Long clientId) {
        ClientPO client = clientMapper.getById(realmId, clientId);
        Verifies.notNull(client, "委托方不存在");
        return EnhancedBeanUtils.createAndCopy(client, ClientVO.class);
    }

    @Transactional
    public void update(Long realmId, Long clientId, ClientUpdateParam param) {
        ClientPO client = clientMapper.getById(realmId, clientId);
        Verifies.notNull(client, "委托方不存在");

        try {
            clientMapper.update(null, Wrappers.<ClientPO>lambdaUpdate()
                    .set(StringUtils.isNotBlank(param.getName()), ClientPO::getName, param.getName())
                    .set(param.getDescription() != null, ClientPO::getDescription, param.getDescription())
                    .set(param.getIsEnabled() != null, ClientPO::getIsEnabled, param.getIsEnabled())
                    .eq(ClientPO::getClientId, clientId)
            );
        } catch (DuplicateKeyException e) {
            throw new ParamException("名称已存在");
        }

    }

}
