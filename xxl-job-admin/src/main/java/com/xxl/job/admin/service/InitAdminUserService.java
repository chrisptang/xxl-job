package com.xxl.job.admin.service;

import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.dao.XxlJobUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * Used to init initial 'admin' user name and password, according to configurations:
 * xxl.job.login.username=admin
 * xxl.job.login.password=123456
 */
@Component
public class InitAdminUserService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(InitAdminUserService.class);

    @Value("${xxl.job.login.username:admin}")
    private String loginUsername;

    @Value("${xxl.job.login.password:123admin456}")
    private String loginPassword;

    @Autowired
    private XxlJobUserDao xxlJobUserDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        XxlJobUser user = xxlJobUserDao.loadByUserName(loginUsername);
        if (user != null && user.getType() != 0) {
            logger.warn("Either user has been removed or updated:" + user);
            return;
        }
        if (null == user) {
            //create new user:
            user = new XxlJobUser();
            user.setUsername(loginUsername);
            String passwordMd5 = DigestUtils.md5DigestAsHex(loginPassword.getBytes());
            user.setPassword(passwordMd5);
            user.setRole(1);
            user.setType(1);

            xxlJobUserDao.save(user);
        } else if (user.getType() == 0) {
            //update password;
            String passwordMd5 = DigestUtils.md5DigestAsHex(loginPassword.getBytes());
            user.setPassword(passwordMd5);
            user.setType(1);
            xxlJobUserDao.update(user);
        }
        logger.warn("System initial user has been updated:\n" + xxlJobUserDao.loadByUserName(loginUsername));
    }
}
