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

import net.guerlab.loadbalancer.ILoadBalancer;
import net.guerlab.loadbalancer.TargetWrapper;
import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.handler.SendHandler;

/**
 * 短信发送负载均衡
 *
 * @author guer
 */
public interface SmsSenderLoadBalancer extends ILoadBalancer<SendHandler, NoticeData> {

    /**
     * 按照业务类型支持进行选择过滤
     *
     * @param targetWrapper
     *         发送处理包装代理对黄
     * @param noticeData
     *         发送数据
     * @return 是否允许使用该发送类型
     */
    static boolean chooseFilter(TargetWrapper<SendHandler> targetWrapper, NoticeData noticeData) {
        if (noticeData.getType() == null || targetWrapper.getTarget() == null) {
            return false;
        }
        return targetWrapper.getTarget().acceptSend(noticeData.getType());
    }
}
