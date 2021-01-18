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

package org.limbo.doorkeeper.server.support.authc;

import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.constants.Intention;
import org.limbo.doorkeeper.api.constants.SessionConstants;
import org.limbo.doorkeeper.api.model.param.auth.AuthenticationUriCheckParam;
import org.limbo.doorkeeper.server.constants.DoorkeeperConstants;
import org.limbo.doorkeeper.server.dao.*;
import org.limbo.doorkeeper.server.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Devil
 * @date 2020/11/24 19:29
 */
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private UriAllowExecutor uriAllowExecutor;

    /**
     * 校验管理端权限 匹配 /admin/realm/**
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 判断 url 是否有对应权限
        String token = request.getHeader(SessionConstants.TOKEN_HEADER);
        Long userId = JWT.decode(token).getClaim("userId").asLong();
        User user = userMapper.selectById(userId);

        Realm dkRealm = realmMapper.getDoorkeeperRealm();

        // 判断用户是否属于dk域
        if (!dkRealm.getRealmId().equals(user.getRealmId())) {
            return false;
        }

        // 判断是不是DK的REALM admin
        Role dkAdmin = roleMapper.getByName(dkRealm.getRealmId(), DoorkeeperConstants.DEFAULT_ID, DoorkeeperConstants.ADMIN);
        if (dkAdmin != null) {
            UserRole userRole = userRoleMapper.selectOne(Wrappers.<UserRole>lambdaQuery()
                    .eq(UserRole::getUserId, user.getUserId())
                    .eq(UserRole::getRoleId, dkAdmin.getRoleId())
            );
            if (userRole != null) {
                return true;
            }
        }

        // 判断uri权限
        NativeWebRequest webRequest = new ServletWebRequest(request);
        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        String realmIdStr = uriTemplateVars.get(DoorkeeperConstants.REALM_ID);

        Realm realm = realmMapper.selectById(Long.valueOf(realmIdStr));

        // 获取对应的client
        Client client = clientMapper.getByName(dkRealm.getRealmId(), realm.getName());

        AuthenticationUriCheckParam param = new AuthenticationUriCheckParam();
        param.setUris(Collections.singletonList(request.getRequestURI()));
        Map<Intention, List<String>> intentionListMap = uriAllowExecutor.accessAllowed(user.getUserId(), client.getClientId(), param);

        if (intentionListMap.get(Intention.ALLOW).size() <= 0) {
            throw new AuthenticationException();
        }

        return true;
    }

}
