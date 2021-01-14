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

package org.limbo.doorkeeper.server.service.policy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.api.exception.ParamException;
import org.limbo.doorkeeper.api.model.Page;
import org.limbo.doorkeeper.api.model.param.policy.PolicyAddParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyBatchUpdateParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyQueryParam;
import org.limbo.doorkeeper.api.model.param.policy.PolicyUpdateParam;
import org.limbo.doorkeeper.api.model.vo.policy.PolicyVO;
import org.limbo.doorkeeper.server.dao.ClientMapper;
import org.limbo.doorkeeper.server.dao.policy.PolicyMapper;
import org.limbo.doorkeeper.server.entity.Client;
import org.limbo.doorkeeper.server.entity.policy.Policy;
import org.limbo.doorkeeper.server.utils.EnhancedBeanUtils;
import org.limbo.doorkeeper.server.utils.MyBatisPlusUtils;
import org.limbo.doorkeeper.server.utils.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Devil
 * @date 2021/1/6 5:16 下午
 */
@Service
public class PolicyService {

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private PolicyRoleService policyRoleService;

    @Autowired
    private PolicyUserService policyUserService;

    @Autowired
    private PolicyParamService policyParamService;

    @Autowired
    private ClientMapper clientMapper;

    @Transactional
    public PolicyVO add(Long realmId, Long clientId, PolicyAddParam param) {
        Verifies.notNull(param.getType(), "策略类型不存在");

        Client client = clientMapper.getById(realmId, clientId);
        Verifies.notNull(client, "委托方不存在");

        Policy policy = EnhancedBeanUtils.createAndCopy(param, Policy.class);
        policy.setRealmId(client.getRealmId());
        policy.setClientId(client.getClientId());
        try {
            policyMapper.insert(policy);
        } catch (DuplicateKeyException e) {
            throw new ParamException("策略已存在");
        }
        switch (param.getType()) {
            case ROLE:
                policyRoleService.batchSave(policy.getPolicyId(), param.getRoles());
                break;
            case USER:
                policyUserService.batchSave(policy.getPolicyId(), param.getUsers());
                break;
            case PARAM:
                policyParamService.batchSave(policy.getPolicyId(), param.getParams());
                break;
        }
        return EnhancedBeanUtils.createAndCopy(policy, PolicyVO.class);
    }

    @Transactional
    public void batchUpdate(Long realmId, Long clientId, PolicyBatchUpdateParam param) {
        switch (param.getType()) {
            case UPDATE:
                policyMapper.update(null, Wrappers.<Policy>lambdaUpdate()
                        .set(param.getIsEnabled() != null, Policy::getIsEnabled, param.getIsEnabled())
                        .in(Policy::getPolicyId, param.getPolicyIds())
                        .eq(Policy::getRealmId, realmId)
                        .eq(Policy::getClientId, clientId)
                );
            default:
                break;
        }
    }

    public Page<PolicyVO> page(Long realmId, Long clientId, PolicyQueryParam param) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Policy> mpage = MyBatisPlusUtils.pageOf(param);
        mpage = policyMapper.selectPage(mpage, Wrappers.<Policy>lambdaQuery()
                .eq(Policy::getRealmId, realmId)
                .eq(Policy::getClientId, clientId)
                .eq(StringUtils.isNotBlank(param.getName()), Policy::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getDimName()), Policy::getName, param.getDimName())
                .eq(param.getIsEnabled() != null, Policy::getIsEnabled, param.getIsEnabled())
                .eq(param.getType() != null, Policy::getType, param.getType())
                .orderByDesc(Policy::getPolicyId)
        );

        param.setTotal(mpage.getTotal());
        param.setData(EnhancedBeanUtils.createAndCopyList(mpage.getRecords(), PolicyVO.class));
        return param;
    }

    public PolicyVO get(Long realmId, Long clientId, Long policyId) {
        Policy policy = policyMapper.getById(realmId, clientId, policyId);
        Verifies.notNull(policy, "策略不存在");
        PolicyVO result = EnhancedBeanUtils.createAndCopy(policy, PolicyVO.class);
        switch (policy.getType()) {
            case ROLE:
                result.setRoles(policyRoleService.getByPolicy(policyId));
                break;
            case USER:
                result.setUsers(policyUserService.getByPolicy(policyId));
                break;
            case PARAM:
                result.setParams(policyParamService.getByPolicy(policyId));
                break;
        }

        return result;
    }

    @Transactional
    public void update(Long realmId, Long clientId, Long policyId, PolicyUpdateParam param) {
        Policy policy = policyMapper.getById(realmId, clientId, policyId);
        Verifies.notNull(policy, "策略不存在");

        policyMapper.update(null, Wrappers.<Policy>lambdaUpdate()
                .set(param.getDescription() != null, Policy::getDescription, param.getDescription())
                .set(param.getLogic() != null, Policy::getLogic, param.getLogic())
                .set(param.getIntention() != null, Policy::getIntention, param.getIntention())
                .set(param.getIsEnabled() != null, Policy::getIsEnabled, param.getIsEnabled())
                .eq(Policy::getPolicyId, policyId)
        );

        switch (policy.getType()) {
            case ROLE:
                policyRoleService.update(policyId, param.getRoles());
                break;
            case USER:
                policyUserService.update(policyId, param.getUsers());
                break;
            case PARAM:
                policyParamService.update(policyId, param.getParams());
                break;
        }
    }

}