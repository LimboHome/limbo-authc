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

package org.limbo.doorkeeper.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.botaruibo.xvcode.generator.Generator;
import com.github.botaruibo.xvcode.generator.GifVCGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.limbo.doorkeeper.admin.config.DoorkeeperProperties;
import org.limbo.doorkeeper.admin.dao.AdminMapper;
import org.limbo.doorkeeper.admin.dao.AdminProjectMapper;
import org.limbo.doorkeeper.admin.entity.Admin;
import org.limbo.doorkeeper.admin.entity.AdminProject;
import org.limbo.doorkeeper.admin.model.param.LoginParam;
import org.limbo.doorkeeper.admin.model.vo.CaptchaVO;
import org.limbo.doorkeeper.admin.service.LoginService;
import org.limbo.doorkeeper.admin.session.AdminSession;
import org.limbo.doorkeeper.admin.session.RedisSessionDAO;
import org.limbo.doorkeeper.admin.session.support.AbstractSession;
import org.limbo.doorkeeper.admin.session.support.SessionAccount;
import org.limbo.doorkeeper.admin.utils.MD5Utils;
import org.limbo.doorkeeper.admin.utils.UUIDUtils;
import org.limbo.doorkeeper.admin.utils.Verifies;
import org.limbo.doorkeeper.api.client.AccountClient;
import org.limbo.doorkeeper.api.client.ProjectClient;
import org.limbo.doorkeeper.api.model.Response;
import org.limbo.doorkeeper.api.model.vo.AccountVO;
import org.limbo.doorkeeper.api.model.vo.ProjectVO;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Devil
 * @date 2020/11/23 8:04 PM
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    private static final String CAPTCHA_REDIS_KEY = "DOORKEEPER_CAPTCHA::TOKEN";

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisSessionDAO sessionDAO;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminProjectMapper adminProjectMapper;

    @Autowired
    private ProjectClient projectClient;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private DoorkeeperProperties doorkeeperProperties;

    @Override
    public AbstractSession login(LoginParam param) {
        Verifies.verify(verifyCaptcha(param.getCaptchaToken(), param.getCaptcha()), "验证码错误！");

        Admin admin = adminMapper.selectOne(Wrappers.<Admin>lambdaQuery()
                .eq(Admin::getUsername, param.getUsername())
        );
        Verifies.notNull(admin, "账户不存在");
        Verifies.verify(MD5Utils.verify(param.getPassword(), admin.getPassword()), "用户名或密码错误！");

        SessionAccount sessionAccount = new SessionAccount();
        sessionAccount.setAccountId(admin.getAccountId());
        sessionAccount.setNickname(admin.getNickname());
        // 选中当前项目
        sessionAccount.setCurrentProjectId(doorkeeperProperties.getProjectId());
        // 先创建会话让 feign拦截器可以获取到信息
        sessionDAO.createSession(sessionAccount);

        Response<AccountVO> accountVOResponse = accountClient.get(admin.getAccountId());
        Verifies.verify(accountVOResponse.ok(), "远程调用失败");
        Verifies.notNull(accountVOResponse.getData(), "账户不存在");

        AccountVO accountVO = accountVOResponse.getData();
        sessionAccount.setProjectId(accountVO.getProjectId());
        sessionAccount.setIsSuperAdmin(accountVO.getIsSuperAdmin());
        sessionAccount.setIsAdmin(accountVO.getIsAdmin());

        List<AdminProject> projects;
        if (sessionAccount.getIsAdmin()) { // 管理员有所有项目权限
            Response<List<ProjectVO>> all = projectClient.getAll();
            projects = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(all.getData())) {
                for (ProjectVO projectVO : all.getData()) {
                    AdminProject project = new AdminProject();
                    project.setAccountId(admin.getAccountId());
                    project.setProjectId(projectVO.getProjectId());
                    project.setProjectName(projectVO.getProjectName());
                    projects.add(project);
                }
            }
        } else {
            projects = adminProjectMapper.getByAccount(admin.getAccountId());
        }

        if (CollectionUtils.isNotEmpty(projects)) {
            sessionAccount.setCurrentProjectId(projects.get(0).getProjectId());
            sessionAccount.setCurrentProjectName(projects.get(0).getProjectName());
        }

        return sessionDAO.createSession(sessionAccount);
    }

    @Override
    public CaptchaVO generateCaptcha() {
        Generator generator = new GifVCGenerator(200, 40, 5,
                new Font("Verdana", 3, 28), 4, 1000);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ByteArrayOutputStream os = (ByteArrayOutputStream) generator.write2out(baos);
             ByteArrayInputStream swapStream = new ByteArrayInputStream(os.toByteArray())) {

            // 生成
            byte[] data = new byte[swapStream.available()];
            IOUtils.read(swapStream, data);
            CaptchaVO captcha = new CaptchaVO(UUIDUtils.get(),
                    "data:image/gif;base64," + Base64.getEncoder().encodeToString(data));

            // 缓存，2分钟内token有效
            RBucket<String> bucket = redissonClient.getBucket(CAPTCHA_REDIS_KEY + "::" + captcha.getToken());
            bucket.set(generator.text(), 2, TimeUnit.MINUTES);

            return captcha;
        } catch (IOException e) {
            log.error("获取验证码失败", e);
            throw new IllegalStateException("生成验证码失败！", e);
        }
    }

    private boolean verifyCaptcha(String token, String captcha) {
        RBucket<String> bucket = redissonClient.getBucket(CAPTCHA_REDIS_KEY + "::" + token);
        if (bucket != null) {
            String cachedCaptcha = bucket.get();
            bucket.delete();
            return StringUtils.equalsIgnoreCase(cachedCaptcha, captcha);
        }

        return false;
    }
}
