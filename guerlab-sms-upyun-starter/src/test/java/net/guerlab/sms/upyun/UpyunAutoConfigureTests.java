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

package net.guerlab.sms.upyun;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import net.guerlab.sms.server.loadbalancer.RandomSmsLoadBalancer;
import net.guerlab.sms.server.spring.autoconfigure.SmsAutoConfiguration;

public class UpyunAutoConfigureTests {

	private AnnotationConfigApplicationContext context;

	@Before
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
		context.registerBean("objectMapper", ObjectMapper.class);
		context.registerBean("smsSenderLoadbalancer", RandomSmsLoadBalancer.class);
		context.register(SmsAutoConfiguration.class);
		TestPropertyValues.of("sms.upyun.token=token").applyTo(context);
		TestPropertyValues.of("sms.upyun.templates.test=templateId").applyTo(context);
		TestPropertyValues.of("sms.upyun.params-orders.test=code").applyTo(context);
	}

	@After
	public void tearDown() {
		Optional.of(context).ifPresent(AnnotationConfigApplicationContext::close);
	}

	@Test
	public void testConditionalOnPropertyNull() {
		context.register(UpyunAutoConfigure.class);
		context.refresh();
		Assert.assertNotNull(context.getBean(UpyunSendHandler.class));
	}

	@Test
	public void testConditionalOnPropertyEmpty() {
		TestPropertyValues.of("sms.upyun.enable=").applyTo(context);
		context.register(UpyunAutoConfigure.class);
		context.refresh();
		Assert.assertNotNull(context.getBean(UpyunSendHandler.class));
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void testConditionalOnPropertyFalse() {
		TestPropertyValues.of("sms.upyun.enable=false").applyTo(context);
		context.register(UpyunAutoConfigure.class);
		context.refresh();
		context.getBean(UpyunSendHandler.class);
	}

	@Test
	public void testConditionalOnPropertyTrue() {
		TestPropertyValues.of("sms.upyun.enable=true").applyTo(context);
		context.register(UpyunAutoConfigure.class);
		context.refresh();
		Assert.assertNotNull(context.getBean(UpyunSendHandler.class));
	}
}
