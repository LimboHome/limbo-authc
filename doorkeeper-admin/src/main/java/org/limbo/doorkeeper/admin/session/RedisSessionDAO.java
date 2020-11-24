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

package org.limbo.doorkeeper.admin.session;

import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.admin.session.support.AbstractSessionDAO;
import org.limbo.doorkeeper.admin.session.support.SecurityDigest;
import org.limbo.doorkeeper.admin.session.support.SessionAccount;
import org.limbo.doorkeeper.admin.utils.JacksonUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author Devil
 * @date 2020/11/24 10:04 AM
 */
public class RedisSessionDAO extends AbstractSessionDAO<AdminSession> {

    private RedissonClient redissonClient;

    private String sessionPrefix;

    public RedisSessionDAO(RedissonClient redissonClient) {
        super(2, TimeUnit.HOURS);
        this.redissonClient = redissonClient;
        this.sessionPrefix = "Doorkeeper-Session-";
    }

    @Override
    public void touchSession(String sessionId) {
        redissonClient.getBucket(getSessionPrefix() + sessionId).expire(sessionExpiry, sessionExpiryUnit);
    }

    @Override
    public void save(AdminSession session) {
        String sessionId = session.getSessionId();
        redissonClient.getBucket(getSessionPrefix() + sessionId)
                .set(JacksonUtil.toJSONString(session), sessionExpiry, sessionExpiryUnit);
    }

    @Override
    protected AdminSession create(String sessionId, SecurityDigest securityDigest, SessionAccount sessionAccount) {
        return new AdminSession(sessionId, securityDigest, sessionAccount);
    }

    @Override
    protected AdminSession read(String sessionId) {
        if (StringUtils.isBlank(sessionId)) {
            return null;
        }

        String sessionJson = (String) redissonClient.getBucket(getSessionPrefix() + sessionId).get();
        if (StringUtils.isBlank(sessionJson)) {
            return null;
        }

        return JacksonUtil.parseObject(sessionJson, AdminSession.class);
    }

    @Override
    protected AdminSession destroy(String sessionId) {
        RBucket<String> bucket = redissonClient.getBucket(getSessionPrefix() + sessionId);
        String sessionJson = bucket.get();
        if (StringUtils.isBlank(sessionJson)) {
            return null;
        }
        bucket.delete();
        return JacksonUtil.parseObject(sessionJson, AdminSession.class);
    }

    @Override
    protected String getSessionPrefix() {
        return sessionPrefix;
    }
}