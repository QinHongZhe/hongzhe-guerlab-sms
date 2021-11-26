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
import net.guerlab.sms.server.service.NoticeService;
import net.guerlab.sms.server.service.VerificationCodeService;
import net.guerlab.sms.server.spring.autoconfigure.SmsAutoConfiguration;
import net.guerlab.sms.server.spring.webmvc.controller.SmsController;
import net.guerlab.sms.server.spring.webmvc.properties.SmsWebmvcProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信webmvc自动配置
 *
 * @author guer
 */
@Slf4j
@Configuration
@AutoConfigureAfter(SmsAutoConfiguration.class)
@EnableConfigurationProperties(SmsWebmvcProperties.class)
@ConditionalOnProperty(prefix = SmsWebmvcProperties.PREFIX, name = "enable", havingValue = "true")
public class SmsWebmvcConfiguration {

    @Bean
    @ConditionalOnMissingBean(SmsController.class)
    public SmsController smsController(VerificationCodeService verificationCodeService, NoticeService noticeService) {
        return new SmsController(verificationCodeService, noticeService);
    }
}
