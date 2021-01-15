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
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.param.resource.RealmAddParam;
import org.limbo.doorkeeper.api.model.vo.GroupVO;
import org.limbo.doorkeeper.api.model.vo.RealmVO;
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.dao.*;
import org.limbo.doorkeeper.server.entity.*;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Devil
 * @date 2021/1/11 11:17 上午
 */
@Service
public class RealmService {

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private DoorkeeperService doorkeeperService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupUserMapper groupUserMapper;

    @Transactional
    public RealmVO add(Long userId, RealmAddParam param) {
        Realm realm = EnhancedBeanUtils.createAndCopy(param, Realm.class);
        try {
            realmMapper.insert(realm);
        } catch (DuplicateKeyException e) {
            throw new ParamException("域已存在");
        }

        // 初始化realm数据
        doorkeeperService.createRealmData(userId, realm.getRealmId(), realm.getName());

        return EnhancedBeanUtils.createAndCopy(realm, RealmVO.class);
    }

    /**
     * user拥有哪些realm
     */
    public List<RealmVO> userRealms(Long userId) {
        Realm dkRealm = realmMapper.selectOne(Wrappers.<Realm>lambdaQuery()
                .eq(Realm::getName, DoorkeeperConstants.REALM_NAME)
        );

        // 判断是不是DK的REALM admin
        Role dkAdmin = roleMapper.getByName(dkRealm.getRealmId(), DoorkeeperConstants.DEFAULT_PARENT_ID, DoorkeeperConstants.ADMIN);
        if (dkAdmin != null) {
            UserRole userRole = userRoleMapper.selectOne(Wrappers.<UserRole>lambdaQuery()
                    .eq(UserRole::getUserId, userId)
                    .eq(UserRole::getRoleId, dkAdmin.getRoleId())
            );
            if (userRole != null) {
                List<Realm> realms = realmMapper.selectList(Wrappers.emptyWrapper());
                return EnhancedBeanUtils.createAndCopyList(realms, RealmVO.class);
            }
        }

        // 用户加入哪些组了 就显示哪些realm
        GroupVO realmGroup = groupService.getRealmGroup();

        List<Group> groups = groupMapper.selectList(Wrappers.<Group>lambdaQuery()
                .eq(Group::getRealmId, dkRealm.getRealmId())
                .eq(Group::getParentId, realmGroup.getGroupId())
        );

        if (CollectionUtils.isEmpty(groups)) {
            return new ArrayList<>();
        }

        List<GroupUser> groupUsers = groupUserMapper.selectList(Wrappers.<GroupUser>lambdaQuery()
                .eq(GroupUser::getUserId, userId)
                .in(GroupUser::getGroupId, groups.stream().map(Group::getGroupId).collect(Collectors.toList()))
        );

        if (CollectionUtils.isEmpty(groupUsers)) {
            return new ArrayList<>();
        }

        // 找到组名
        List<String> realmNames = new ArrayList<>();
        for (GroupUser groupUser : groupUsers) {
            for (Group group : groups) {
                if (groupUser.getGroupId().equals(group.getGroupId())) {
                    realmNames.add(group.getName());
                    break;
                }
            }
        }

        List<Realm> realms = realmMapper.selectList(Wrappers.<Realm>lambdaQuery()
                .in(Realm::getName, realmNames)
        );
        return EnhancedBeanUtils.createAndCopyList(realms, RealmVO.class);
    }
}
