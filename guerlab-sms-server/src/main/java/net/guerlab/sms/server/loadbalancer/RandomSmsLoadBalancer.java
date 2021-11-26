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
package net.guerlab.sms.server.loadbalancer;

import net.guerlab.loadbalancer.RandomLoadBalancer;
import net.guerlab.loadbalancer.TargetWrapper;
import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.handler.SendHandler;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * random Load Balancer
 *
 * @author guer
 */
public class RandomSmsLoadBalancer extends RandomLoadBalancer<SendHandler, NoticeData>
        implements SmsSenderLoadBalancer {

    public static final String TYPE_NAME = "Random";

    @Nullable
    @Override
    protected SendHandler choose0(List<TargetWrapper<SendHandler>> activeTargetList, NoticeData chooseReferenceObject) {
        List<TargetWrapper<SendHandler>> newActiveTargetList = activeTargetList.stream().filter(wrapper -> SmsSenderLoadBalancer.chooseFilter(wrapper, chooseReferenceObject))
                .collect(Collectors.toList());
        if (newActiveTargetList.isEmpty()) {
            return null;
        }
        return super.choose0(activeTargetList, chooseReferenceObject);
    }
}
