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

package net.guerlab.sms.jdcloud;

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

import net.guerlab.sms.server.loadbalancer.SmsSenderLoadBalancer;
import net.guerlab.sms.server.spring.autoconfigure.SmsAutoConfiguration;

/**
 * 京东云发送端点自动配置.
 *
 * @author guer
 */
@Configuration
@EnableConfigurationProperties(JdCloudProperties.class)
@AutoConfigureAfter(SmsAutoConfiguration.class)
public class JdCloudAutoConfigure {

	/**
	 * 构造京东云发送处理.
	 *
	 * @param properties     配置对象
	 * @param loadbalancer   负载均衡器
	 * @param eventPublisher spring应用事件发布器
	 * @return 京东云发送处理
	 */
	@Bean
	@Conditional(JdCloudSendHandlerCondition.class)
	@ConditionalOnBean(SmsSenderLoadBalancer.class)
	public JdCloudSendHandler jdCloudSendHandler(JdCloudProperties properties, SmsSenderLoadBalancer loadbalancer,
			ApplicationEventPublisher eventPublisher) {
		JdCloudSendHandler handler = new JdCloudSendHandler(properties, eventPublisher);
		loadbalancer.addTarget(handler, true);
		loadbalancer.setWeight(handler, properties.getWeight());
		return handler;
	}

	public static class JdCloudSendHandlerCondition implements Condition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			Boolean enable = context.getEnvironment().getProperty("sms.jdcloud.enable", Boolean.class);
			return enable == null || enable;
		}
	}

}
