/*
 * Copyright 2018-2022 the original author or authors.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.guerlab.sms.server.spring.webmvc.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import net.guerlab.sms.core.domain.NoticeInfo;
import net.guerlab.sms.core.domain.VerifyInfo;
import net.guerlab.sms.core.utils.StringUtils;
import net.guerlab.sms.server.properties.SmsWebConfig;
import net.guerlab.sms.server.spring.webmvc.controller.SmsController;
import net.guerlab.sms.server.spring.webmvc.properties.SmsWebmvcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * 短信控制器路径自动配置
 *
 * @author guer
 */
@Slf4j
@Configuration
@AutoConfigureAfter(SmsWebmvcConfiguration.class)
@ConditionalOnProperty(prefix = SmsWebmvcProperties.PREFIX, name = "enable", havingValue = "true")
public class SmsWebmvcPathConfiguration {

    private static String getBasePath(SmsWebConfig properties) {
        String bathPath = StringUtils.trimToNull(properties.getBasePath());

        return bathPath == null ? SmsWebConfig.DEFAULT_BASE_PATH : bathPath;
    }

    /**
     * 构造短信Controller
     *
     * @param controller
     *         短信Controller
     * @param mapping
     *         RequestMappingHandlerMapping
     * @param properties
     *         短信Web配置
     * @throws NoSuchMethodException
     *         if a matching method is not found
     *         or if the name is "&lt;init&gt;"or "&lt;clinit&gt;".
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and
     *         the caller's class loader is not the same as or an
     *         ancestor of the class loader for the current class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of this class.
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    @ConditionalOnBean(RequestMappingHandlerMapping.class)
    public void smsController(SmsWebConfig properties, RequestMappingHandlerMapping mapping, SmsController controller)
            throws NoSuchMethodException, SecurityException {
        String bathPath = getBasePath(properties);

        if (properties.isEnableSend()) {
            Method sendMethod = SmsController.class.getMethod("sendVerificationCode", String.class);
            RequestMappingInfo sendInfo = RequestMappingInfo.paths(bathPath + "/verificationCode/{phone}")
                    .methods(RequestMethod.POST).build();
            mapping.registerMapping(sendInfo, controller, sendMethod);
            log.debug("registerMapping: {}", sendInfo);
        } else {
            log.debug("not register: sendInfo");
        }
        if (properties.isEnableGet()) {
            Method getMethod = SmsController.class.getMethod("getVerificationCode", String.class, String.class);
            RequestMappingInfo getInfo = RequestMappingInfo.paths(bathPath + "/verificationCode/{phone}")
                    .methods(RequestMethod.GET).produces("application/json").build();
            mapping.registerMapping(getInfo, controller, getMethod);
            log.debug("registerMapping: {}", getInfo);
        } else {
            log.debug("not register: getInfo");
        }
        if (properties.isEnableVerify()) {
            Method verifyMethod = SmsController.class.getMethod("verifyVerificationCode", VerifyInfo.class);
            RequestMappingInfo verifyInfo = RequestMappingInfo.paths(bathPath + "/verificationCode")
                    .methods(RequestMethod.POST).build();
            mapping.registerMapping(verifyInfo, controller, verifyMethod);
            log.debug("registerMapping: {}", verifyInfo);
        } else {
            log.debug("not register: verifyInfo");
        }
        if (properties.isEnableNotice()) {
            Method noticeMethod = SmsController.class.getMethod("sendNotice", NoticeInfo.class);
            RequestMappingInfo noticeInfo = RequestMappingInfo.paths(bathPath + "/notice").methods(RequestMethod.PUT)
                    .build();
            mapping.registerMapping(noticeInfo, controller, noticeMethod);
            log.debug("registerMapping: {}", noticeInfo);
        } else {
            log.debug("not register: noticeInfo");
        }
    }
}
