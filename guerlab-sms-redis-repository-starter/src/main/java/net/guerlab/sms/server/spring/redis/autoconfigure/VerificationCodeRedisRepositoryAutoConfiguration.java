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
import net.guerlab.sms.server.repository.VerificationCodeRepository;
import net.guerlab.sms.server.spring.autoconfigure.SmsAutoConfiguration;
import net.guerlab.sms.server.spring.redis.properties.RedisProperties;
import net.guerlab.sms.server.spring.redis.repository.VerificationCodeRedisRepository;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 验证码redis储存实现自动配置
 *
 * @author guer
 */
@Configuration
@AutoConfigureAfter(SmsAutoConfiguration.class)
@EnableConfigurationProperties(RedisProperties.class)
public class VerificationCodeRedisRepositoryAutoConfiguration {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public VerificationCodeRepository verificationCodeRedisRepository(RedisProperties properties,
            RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        return new VerificationCodeRedisRepository(properties, redisTemplate, objectMapper);
    }
}
