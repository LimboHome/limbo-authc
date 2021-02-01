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

package org.limbo.doorkeeper.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.server.service.DoorkeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Devil
 * @date 2021/2/1 5:04 下午
 */
@Slf4j
@RestController
@RequestMapping("/init")
public class InitController {

    @Autowired
    private DoorkeeperService doorkeeperService;

    @GetMapping
    public Response<String> init() {
        doorkeeperService.initDoorkeeper();
        return Response.success();
    }

}
