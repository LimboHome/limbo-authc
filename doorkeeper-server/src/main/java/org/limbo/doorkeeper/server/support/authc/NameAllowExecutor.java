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

package org.limbo.doorkeeper.server.support.authc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.model.param.auth.AuthenticationNameCheckParam;
import org.limbo.doorkeeper.api.model.vo.PermissionVO;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.dao.PermissionResourceMapper;
import org.limbo.doorkeeper.server.dao.ResourceMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.entity.PermissionResource;
import org.limbo.doorkeeper.server.entity.Resource;
import org.limbo.doorkeeper.server.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/14 10:31 上午
 */
@Component
public class NameAllowExecutor extends AbstractAllowedExecutor<AuthenticationNameCheckParam, String> {


    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private PermissionResourceMapper permissionResourceMapper;

    @Autowired
    private PermissionService permissionService;

    @Override
    public Map<Intention, List<String>> accessAllowed(Long userId, Long clientId, AuthenticationNameCheckParam param) {
        // 获取对应的client
        Client client = clientMapper.selectById(clientId);

        Map<Intention, List<String>> result = new HashMap<>();
        result.put(Intention.ALLOW, new ArrayList<>());
        result.put(Intention.REFUSE, new ArrayList<>());

        if (CollectionUtils.isEmpty(param.getNames())) {
            return result;
        }

        // 对于每个名字进行匹配
        for (String name : param.getNames()) {

            Resource resource = resourceMapper.getByName(client.getRealmId(), client.getClientId(), name);
            if (resource == null) {
                result.get(Intention.REFUSE).add(name);
                continue;
            }

            List<PermissionResource> permissionResources = permissionResourceMapper.selectList(Wrappers.<PermissionResource>lambdaQuery()
                    .eq(PermissionResource::getResourceId, resource.getResourceId())
            );
            if (CollectionUtils.isEmpty(permissionResources)) {
                result.get(Intention.REFUSE).add(name);
                continue;
            }
            Set<Long> permissionIds = permissionResources.stream().map(PermissionResource::getPermissionId).collect(Collectors.toSet());
            // 找到权限以及对应的策略
            List<PermissionVO> permissions = new ArrayList<>();
            for (Long permissionId : permissionIds) {
                PermissionVO permissionVO = permissionService.get(client.getRealmId(), client.getClientId(), permissionId);
                if (permissionVO != null) {
                    permissions.add(permissionVO);
                }
            }
            if (CollectionUtils.isEmpty(permissions)) {
                result.get(Intention.REFUSE).add(name);
                continue;
            }
            Map<Intention, Set<PermissionVO>> intentionPermissions = permissions.stream().collect(Collectors.groupingBy(
                    permissionVO -> Intention.parse(permissionVO.getIntention()),
                    Collectors.mapping(p -> p, Collectors.toSet())
            ));

            // 找到拦截的权限 如果满足 返回false
            Set<PermissionVO> refuse = intentionPermissions.get(Intention.REFUSE);
            if (CollectionUtils.isNotEmpty(refuse)) {
                boolean r = false;
                for (PermissionVO permissionVO : refuse) {
                    if (permissionExecute(userId, client.getRealmId(), client.getClientId(), permissionVO, param.getParams())) {
                        r = true;
                        break;
                    }
                }
                if (r) {
                    result.get(Intention.REFUSE).add(name);
                    continue;
                }
            }


            // 找到放行的权限 如果满足 返回true
            Set<PermissionVO> allow = intentionPermissions.get(Intention.ALLOW);
            if (CollectionUtils.isNotEmpty(allow)) {
                boolean a = false;
                for (PermissionVO permissionVO : allow) {
                    if (permissionExecute(userId, client.getRealmId(), client.getClientId(), permissionVO, param.getParams())) {
                        a = true;
                        break;
                    }
                }
                if (a) {
                    result.get(Intention.ALLOW).add(name);
                    continue;
                }
            }

            result.get(Intention.REFUSE).add(name);

        }

        return result;
    }

}
