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

package org.limbo.doorkeeper.server.infrastructure.checker;

import org.limbo.doorkeeper.server.infrastructure.mapper.GroupMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.GroupRoleMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.GroupUserMapper;
import org.limbo.doorkeeper.server.infrastructure.mapper.UserRoleMapper;
import org.limbo.doorkeeper.server.infrastructure.po.UserPO;
import org.limbo.doorkeeper.server.infrastructure.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Devil
 * @date 2021/6/2 10:57 上午
 */
@Component
public class PolicyCheckerFactory {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupUserMapper groupUserMapper;

    @Autowired
    private GroupRoleMapper groupRoleMapper;

    public PolicyChecker newPolicyChecker(UserPO user) {
        Verifies.notNull(user, "校验用户不能为空");
        return new PolicyChecker(user, groupMapper, groupUserMapper, groupRoleMapper, userRoleMapper);
    }

}
