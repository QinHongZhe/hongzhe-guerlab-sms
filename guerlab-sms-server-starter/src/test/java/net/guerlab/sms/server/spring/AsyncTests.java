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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.handler.SendHandler;
import net.guerlab.sms.server.loadbalancer.SmsSenderLoadBalancer;
import net.guerlab.sms.server.service.NoticeService;
import net.guerlab.sms.server.spring.autoconfigure.SmsAutoConfiguration;

/**
 * 自动配置测试.
 *
 * @author guer
 */
@Slf4j
public class AsyncTests {

	private static final CountDownLatch countDownLatch = new CountDownLatch(1);

	private static Thread runnerThread;

	private AnnotationConfigApplicationContext context;

	@Before
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
		context.register(SmsAutoConfiguration.class);
		context.register(TestHandlerAutoConfigure.class);
	}

	@After
	public void tearDown() {
		Optional.of(context).ifPresent(AnnotationConfigApplicationContext::close);
	}

	@Test
	public void enabled() {
		TestPropertyValues.of("sms.async.enable=true").applyTo(context);
		context.refresh();
		NoticeService service = context.getBean(NoticeService.class);
		NoticeData noticeData = new NoticeData();
		noticeData.setType("foo");
		service.asyncSend(noticeData, "test");
		try {
			countDownLatch.await();
		}
		catch (Exception e) {
			// ignore
		}
		Assert.assertNotEquals(Thread.currentThread(), runnerThread);
	}

	@Test
	public void disabled() {
		TestPropertyValues.of("sms.async.enable=false").applyTo(context);
		context.refresh();
		NoticeService service = context.getBean(NoticeService.class);
		NoticeData noticeData = new NoticeData();
		noticeData.setType("foo");
		service.asyncSend(noticeData, "test");
		try {
			countDownLatch.await();
		}
		catch (Exception e) {
			// ignore
		}
		Assert.assertEquals(Thread.currentThread(), runnerThread);
	}

	public static class TestHandlerAutoConfigure {

		@Bean
		@ConditionalOnBean(SmsSenderLoadBalancer.class)
		public TestSendHandler testSendHandler(SmsSenderLoadBalancer loadbalancer) {
			TestSendHandler handler = new TestSendHandler(Arrays.asList("test", "foo"));
			loadbalancer.addTarget(handler, true);
			loadbalancer.setWeight(handler, 1);
			return handler;
		}
	}

	private static class TestSendHandler implements SendHandler {

		private final List<String> acceptKeys;

		TestSendHandler(List<String> acceptKeys) {
			this.acceptKeys = acceptKeys;
		}

		@Override
		public boolean send(NoticeData noticeData, Collection<String> phones) {
			countDownLatch.countDown();
			runnerThread = Thread.currentThread();
			return true;
		}

		@Override
		public boolean acceptSend(String type) {
			return acceptKeys.contains(type);
		}
	}
}
