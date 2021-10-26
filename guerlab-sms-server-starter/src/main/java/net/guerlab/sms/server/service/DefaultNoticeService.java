/*
 * Copyright 2018-2021 the original author or authors.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.guerlab.sms.server.service;

import lombok.extern.slf4j.Slf4j;
import net.guerlab.loadbalancer.ILoadBalancer;
import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.exception.NotFindSendHandlerException;
import net.guerlab.sms.core.handler.SendHandler;
import net.guerlab.sms.core.utils.StringUtils;
import net.guerlab.sms.server.properties.SmsAsyncProperties;
import net.guerlab.sms.server.properties.SmsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 短信通知服务实现
 *
 * @author guer
 */
@SuppressWarnings("AlibabaServiceOrDaoClassShouldEndWithImpl")
@Slf4j
@Service
public class DefaultNoticeService implements NoticeService {

    private SmsProperties properties;

    private ILoadBalancer<SendHandler, NoticeData> smsSenderLoadbalancer;

    private SmsAsyncProperties asyncProperties;

    private SendAsyncThreadPoolExecutor executor;

    @Override
    public boolean phoneRegValidation(String phone) {
        return StringUtils.isNotBlank(phone) && (StringUtils.isBlank(properties.getReg()) || phone
                .matches(properties.getReg()));
    }

    @Override
    public boolean send(NoticeData noticeData, Collection<String> phones) {
        SendResult result = send0(noticeData, phones);

        if (result.exception != null) {
            throw result.exception;
        }

        return result.result;
    }

    @Override
    public void asyncSend(NoticeData noticeData, Collection<String> phones) {
        if (!asyncProperties.isEnable() || executor == null) {
            send(noticeData, phones);
            return;
        }

        executor.submit(() -> send0(noticeData, phones));
    }

    private SendResult send0(NoticeData noticeData, Collection<String> phones) {
        SendResult result = new SendResult();
        if (phones.isEmpty()) {
            log.debug("phones is empty");
            return result;
        }

        List<String> phoneList = phones.stream().filter(this::phoneRegValidation).collect(Collectors.toList());

        if (phoneList.isEmpty()) {
            log.debug("after filter phones is empty");
            return result;
        }

        SendHandler sendHandler = smsSenderLoadbalancer.choose(noticeData);

        if (sendHandler == null) {
            result.exception = new NotFindSendHandlerException();
            log.debug(result.exception.getLocalizedMessage());
        } else {
            result.result = sendHandler.send(noticeData, phones);
        }

        return result;
    }

    private static class SendResult {

        boolean result;

        RuntimeException exception;
    }

    @Autowired
    public void setProperties(SmsProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setAsyncProperties(SmsAsyncProperties asyncProperties) {
        this.asyncProperties = asyncProperties;
    }

    @Autowired(required = false)
    public void setExecutor(SendAsyncThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setSmsSenderLoadbalancer(ILoadBalancer<SendHandler, NoticeData> smsSenderLoadbalancer) {
        this.smsSenderLoadbalancer = smsSenderLoadbalancer;
    }
}
