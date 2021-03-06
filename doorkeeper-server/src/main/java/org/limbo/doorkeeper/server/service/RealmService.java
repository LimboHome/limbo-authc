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
import org.limbo.doorkeeper.api.model.param.update.RealmUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RealmVO;
import org.limbo.doorkeeper.server.infrastructure.po.RealmPO;
import org.limbo.doorkeeper.server.infrastructure.po.UserPO;
import org.limbo.doorkeeper.server.infrastructure.mapper.RealmMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.UserMapper;
import org.limbo.doorkeeper.server.infrastructure.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.infrastructure.utils.JWTUtil;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Devil
 * @date 2021/1/11 11:17 上午
 */
@Service
public class RealmService {

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private UserMapper userMapper;

    public RealmVO get(Long realmId) {
        RealmPO realm = realmMapper.selectById(realmId);
        return EnhancedBeanUtils.createAndCopy(realm, RealmVO.class);
    }

    public void update(Long realmId, RealmUpdateParam param) {
        realmMapper.update(null, Wrappers.<RealmPO>lambdaUpdate()
                .set(StringUtils.isNotBlank(param.getSecret()), RealmPO::getSecret, param.getSecret())
                .eq(RealmPO::getRealmId, realmId)
        );
    }


    public RealmPO getRealmByToken(String token) {
        Long userId = JWTUtil.getUserId(token);
        UserPO user;
        if (userId != null) {
            user = userMapper.selectById(userId);
        } else {
            String username = JWTUtil.getUsername(token);
            Long realmId = JWTUtil.getRealmId(token);
            user = userMapper.getByUsername(realmId, username);
        }
        Verifies.notNull(user, "用户不存在");
        Verifies.verify(user.getIsEnabled(), "用户未启用");

        RealmPO realm = realmMapper.selectById(user.getRealmId());
        Verifies.notNull(realm, "realm不存在");
        return realm;
    }
}
