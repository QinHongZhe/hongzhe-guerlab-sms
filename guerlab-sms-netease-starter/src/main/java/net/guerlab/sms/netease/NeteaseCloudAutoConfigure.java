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

package net.guerlab.sms.netease;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.client.RestTemplate;

import net.guerlab.sms.server.loadbalancer.SmsSenderLoadBalancer;
import net.guerlab.sms.server.spring.autoconfigure.SmsAutoConfiguration;

/**
 * 网易云信发送端点自动配置.
 *
 * @author guer
 */
@Configuration
@EnableConfigurationProperties(NeteaseCloudProperties.class)
@AutoConfigureAfter(SmsAutoConfiguration.class)
public class NeteaseCloudAutoConfigure {

	/**
	 * 构造网易云信发送处理.
	 *
	 * @param properties     配置对象
	 * @param objectMapper   objectMapper
	 * @param loadbalancer   负载均衡器
	 * @param eventPublisher spring应用事件发布器
	 * @param restTemplate   RestTemplate
	 * @return 网易云信发送处理
	 */
	@Bean
	@Conditional(NeteaseCloudSendHandlerCondition.class)
	@ConditionalOnBean(SmsSenderLoadBalancer.class)
	public NeteaseCloudSendHandler neteaseCloudSendHandler(NeteaseCloudProperties properties, ObjectMapper objectMapper,
			SmsSenderLoadBalancer loadbalancer, ApplicationEventPublisher eventPublisher, RestTemplate restTemplate) {
		NeteaseCloudSendHandler handler = new NeteaseCloudSendHandler(properties, eventPublisher, objectMapper,
				restTemplate);
		loadbalancer.addTarget(handler, true);
		loadbalancer.setWeight(handler, properties.getWeight());
		return handler;
	}

	public static class NeteaseCloudSendHandlerCondition implements Condition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			Boolean enable = context.getEnvironment().getProperty("sms.netease.enable", Boolean.class);
			return enable == null || enable;
		}
	}

}
