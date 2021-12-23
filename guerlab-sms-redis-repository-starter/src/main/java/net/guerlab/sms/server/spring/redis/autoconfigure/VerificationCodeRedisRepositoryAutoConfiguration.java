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
package net.guerlab.sms.server.spring.redis.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.guerlab.sms.server.repository.VerificationCodeRepository;
import net.guerlab.sms.server.spring.autoconfigure.SmsAutoConfiguration;
import net.guerlab.sms.server.spring.autoconfigure.VerificationCodeAutoConfiguration;
import net.guerlab.sms.server.spring.redis.properties.RedisProperties;
import net.guerlab.sms.server.spring.redis.repository.VerificationCodeRedisRepository;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 验证码redis储存实现自动配置
 *
 * @author guer
 */
@Slf4j
@Configuration
@AutoConfigureAfter(SmsAutoConfiguration.class)
@AutoConfigureBefore(VerificationCodeAutoConfiguration.class)
@EnableConfigurationProperties(RedisProperties.class)
public class VerificationCodeRedisRepositoryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(VerificationCodeRepository.class)
    @ConditionalOnBean(StringRedisTemplate.class)
    public VerificationCodeRepository verificationCodeRedisRepository(RedisProperties properties,
            StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        VerificationCodeRepository repository = new VerificationCodeRedisRepository(properties, redisTemplate,
                objectMapper);
        log.debug("create VerificationCodeRepository: Redis");
        return repository;
    }
}
