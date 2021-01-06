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

import org.limbo.doorkeeper.api.model.param.RoleCombineQueryParam;
import org.limbo.doorkeeper.api.model.param.RoleCombineBatchUpdateParam;
import org.limbo.doorkeeper.api.model.vo.RoleCombineVO;

import java.util.List;

/**
 * @author Devil
 * @date 2021/1/5 1:58 下午
 */
public interface RoleCombineService {

    List<RoleCombineVO> list(RoleCombineQueryParam param);

    void batchUpdate(RoleCombineBatchUpdateParam param);

}
