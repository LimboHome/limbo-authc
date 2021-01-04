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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.ClientAddParam;
import org.limbo.doorkeeper.api.model.param.ClientQueryParam;
import org.limbo.doorkeeper.api.model.vo.ClientVO;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.service.ClientService;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Devil
 * @date 2021/1/4 2:59 下午
 */
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientMapper clientMapper;

    @Override
    public ClientVO add(ClientAddParam param) {
        Client client = EnhancedBeanUtils.createAndCopy(param, Client.class);
        clientMapper.insert(client);
        return EnhancedBeanUtils.createAndCopy(client, ClientVO.class);
    }

    @Override
    public Page<ClientVO> page(ClientQueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Client> mpage = MyBatisPlusUtils.pageOf(param);
        LambdaQueryWrapper<Client> condition = Wrappers.<Client>lambdaQuery()
                .like(StringUtils.isNotBlank(param.getName()), Client::getName, param.getName())
                .orderByDesc(Client::getClientId);
        mpage = clientMapper.selectPage(mpage, condition);

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), ClientVO.class));
        return param;
    }
}
