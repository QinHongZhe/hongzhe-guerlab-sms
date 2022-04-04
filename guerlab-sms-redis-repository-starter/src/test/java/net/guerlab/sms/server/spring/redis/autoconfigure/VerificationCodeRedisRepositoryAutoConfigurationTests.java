/*
 * Copyright 2018-2022 guerlab.net and other contributors.
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

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import net.guerlab.sms.server.loadbalancer.RandomSmsLoadBalancer;
import net.guerlab.sms.server.repository.VerificationCodeRepository;
import net.guerlab.sms.server.spring.autoconfigure.SmsAutoConfiguration;
import net.guerlab.sms.server.spring.autoconfigure.VerificationCodeAutoConfiguration;
import net.guerlab.sms.server.spring.redis.repository.VerificationCodeRedisRepository;

/**
 * 验证码redis储存实现自动配置测试.
 *
 * @author guer
 */
public class VerificationCodeRedisRepositoryAutoConfigurationTests {

	private AnnotationConfigApplicationContext context;

	@Before
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
		context.registerBean("objectMapper", ObjectMapper.class);
		context.registerBean("smsSenderLoadbalancer", RandomSmsLoadBalancer.class);
	}

	@After
	public void tearDown() {
		Optional.of(context).ifPresent(AnnotationConfigApplicationContext::close);
	}

	@Test
	public void redis() {
		context.register(RedisAutoConfiguration.class);
		context.register(SmsAutoConfiguration.class);
		context.register(VerificationCodeRedisRepositoryAutoConfiguration.class);
		context.register(VerificationCodeAutoConfiguration.class);
		context.refresh();
		Assert.assertTrue(context.getBean(VerificationCodeRepository.class) instanceof VerificationCodeRedisRepository);
	}

	@Test
	public void memory() {
		context.register(SmsAutoConfiguration.class);
		context.register(VerificationCodeRedisRepositoryAutoConfiguration.class);
		context.register(VerificationCodeAutoConfiguration.class);
		context.refresh();
		Assert.assertFalse(
				context.getBean(VerificationCodeRepository.class) instanceof VerificationCodeRedisRepository);
	}

	@Test
	public void size() {
		context.register(RedisAutoConfiguration.class);
		context.register(SmsAutoConfiguration.class);
		context.register(VerificationCodeRedisRepositoryAutoConfiguration.class);
		context.register(VerificationCodeAutoConfiguration.class);
		context.refresh();
		Assert.assertEquals(1, context.getBeansOfType(VerificationCodeRepository.class).size());
	}
}
