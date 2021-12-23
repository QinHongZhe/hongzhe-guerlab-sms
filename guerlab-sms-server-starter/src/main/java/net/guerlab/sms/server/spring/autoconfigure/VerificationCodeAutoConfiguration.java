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
package net.guerlab.sms.server.spring.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import net.guerlab.sms.server.properties.VerificationCodeConfig;
import net.guerlab.sms.server.repository.VerificationCodeMemoryRepository;
import net.guerlab.sms.server.repository.VerificationCodeRepository;
import net.guerlab.sms.server.service.*;
import net.guerlab.sms.server.spring.properties.VerificationCodeMemoryRepositoryProperties;
import net.guerlab.sms.server.spring.properties.VerificationCodeProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码服务配置
 *
 * @author guer
 */
@Slf4j
@Configuration
@AutoConfigureAfter(SmsAutoConfiguration.class)
@EnableConfigurationProperties({ VerificationCodeProperties.class, VerificationCodeMemoryRepositoryProperties.class })
public class VerificationCodeAutoConfiguration {

    /**
     * 创建默认验证码生成
     *
     * @param properties
     *         验证码配置
     * @return 默认验证码生成
     */
    @Bean
    @ConditionalOnMissingBean(CodeGenerate.class)
    public CodeGenerate defaultCodeGenerate(VerificationCodeConfig properties) {
        return new DefaultCodeGenerate(properties);
    }

    /**
     * 创建手机验证码服务
     *
     * @param repository
     *         验证码储存接口
     * @param properties
     *         验证码配置
     * @param noticeService
     *         短信通知服务
     * @param codeGenerate
     *         验证码生成
     * @param verificationCodeTypeGenerateProvider
     *         验证码类型生成
     * @return 手机验证码服务
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnMissingBean(VerificationCodeService.class)
    public VerificationCodeService verificationCodeService(VerificationCodeRepository repository,
            VerificationCodeConfig properties, NoticeService noticeService, CodeGenerate codeGenerate,
            ObjectProvider<VerificationCodeTypeGenerate> verificationCodeTypeGenerateProvider) {
        return new DefaultVerificationCodeService(repository, properties, noticeService, codeGenerate,
                verificationCodeTypeGenerateProvider.getIfUnique());
    }

    /**
     * 构造默认验证码储存接口实现
     *
     * @param config
     *         验证码内存储存配置
     * @return 默认验证码储存接口实现
     */
    @Bean
    @ConditionalOnMissingBean(VerificationCodeRepository.class)
    public VerificationCodeRepository verificationCodeMemoryRepository(
            VerificationCodeMemoryRepositoryProperties config) {
        VerificationCodeRepository repository = new VerificationCodeMemoryRepository(config);
        log.debug("create VerificationCodeRepository: Memory");
        return repository;
    }
}
