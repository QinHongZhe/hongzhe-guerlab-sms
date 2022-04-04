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

package net.guerlab.sms.server.loadbalancer;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.Nullable;

import net.guerlab.loadbalancer.TargetWrapper;
import net.guerlab.loadbalancer.WeightRoundRobinLoadBalancer;
import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.handler.SendHandler;

/**
 * weight round-robin Load Balancer.
 *
 * @author guer
 */
public class WeightRoundRobinSmsLoadBalancer extends WeightRoundRobinLoadBalancer<SendHandler, NoticeData>
		implements SmsSenderLoadBalancer {

	/**
	 * 负载均衡类型.
	 */
	public static final String TYPE_NAME = "WeightRoundRobin";

	@Nullable
	@Override
	protected SendHandler choose0(List<TargetWrapper<SendHandler>> activeTargetList, NoticeData chooseReferenceObject) {
		List<TargetWrapper<SendHandler>> newActiveTargetList = activeTargetList.stream()
				.filter(wrapper -> SmsSenderLoadBalancer.chooseFilter(wrapper, chooseReferenceObject))
				.collect(Collectors.toList());
		if (newActiveTargetList.isEmpty()) {
			return null;
		}
		return super.choose0(activeTargetList, chooseReferenceObject);
	}
}

