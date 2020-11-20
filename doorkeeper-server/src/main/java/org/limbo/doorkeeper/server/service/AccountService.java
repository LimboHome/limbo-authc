/*
 *  Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.limbo.doorkeeper.server.service;

import org.limbo.doorkeeper.api.model.param.AccountRegisterParam;
import org.limbo.doorkeeper.api.model.vo.AccountVO;

/**
 * @author Brozen
 * @date 2020/2/27 5:41 PM
 * @email brozen@qq.com
 */
public interface AccountService {

    /**
     * 注册用户
     */
    AccountVO register(AccountRegisterParam param, Boolean isActivated);
}
