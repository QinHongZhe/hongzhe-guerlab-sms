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

package net.guerlab.sms.server.spring;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import net.guerlab.sms.server.properties.RejectPolicy;
import net.guerlab.sms.server.properties.SmsAsyncConfig;
import net.guerlab.sms.server.spring.autoconfigure.SmsAutoConfiguration;

/**
 * 异步配置测试.
 *
 * @author guer
 */
public class AsyncPropertiesTests {

	private AnnotationConfigApplicationContext context;

	@Before
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
		context.register(SmsAutoConfiguration.class);
	}

	@After
	public void tearDown() {
		Optional.of(context).ifPresent(AnnotationConfigApplicationContext::close);
	}

	@Test
	public void enableWithTrue() {
		TestPropertyValues.of("sms.async.enable=true").applyTo(context);
		context.refresh();
		SmsAsyncConfig properties = context.getBean(SmsAsyncConfig.class);
		Assert.assertTrue(properties.isEnable());
	}

	@Test
	public void enableWithFalse() {
		TestPropertyValues.of("sms.async.enable=false").applyTo(context);
		context.refresh();
		SmsAsyncConfig properties = context.getBean(SmsAsyncConfig.class);
		Assert.assertFalse(properties.isEnable());
	}

	@Test
	public void defaultUnit() {
		context.refresh();
		SmsAsyncConfig properties = context.getBean(SmsAsyncConfig.class);
		Assert.assertEquals(properties.getUnit(), TimeUnit.SECONDS);
	}

	@Test
	public void customerUnit() {
		TestPropertyValues.of("sms.async.unit=MINUTES").applyTo(context);
		context.refresh();
		SmsAsyncConfig properties = context.getBean(SmsAsyncConfig.class);
		Assert.assertEquals(properties.getUnit(), TimeUnit.MINUTES);
	}

	@Test
	public void defaultRejectPolicy() {
		context.refresh();
		SmsAsyncConfig properties = context.getBean(SmsAsyncConfig.class);
		Assert.assertEquals(properties.getRejectPolicy(), RejectPolicy.Abort);
	}

	@Test
	public void customerRejectPolicy() {
		TestPropertyValues.of("sms.async.reject-policy=Discard").applyTo(context);
		context.refresh();
		SmsAsyncConfig properties = context.getBean(SmsAsyncConfig.class);
		Assert.assertEquals(properties.getRejectPolicy(), RejectPolicy.Discard);
	}

	@Test
	public void nullRejectPolicy() {
		TestPropertyValues.of("sms.async.reject-policy=").applyTo(context);
		context.refresh();
		SmsAsyncConfig properties = context.getBean(SmsAsyncConfig.class);
		Assert.assertEquals(properties.getRejectPolicy(), RejectPolicy.Abort);
	}
}
