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

package net.guerlab.sms.server.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.Nullable;

import net.guerlab.loadbalancer.ILoadBalancer;
import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.exception.NotFindSendHandlerException;
import net.guerlab.sms.core.exception.SmsException;
import net.guerlab.sms.core.handler.SendHandler;
import net.guerlab.sms.core.utils.StringUtils;
import net.guerlab.sms.server.properties.SmsAsyncConfig;
import net.guerlab.sms.server.properties.SmsConfig;

/**
 * 短信通知服务实现.
 *
 * @author guer
 */
@SuppressWarnings("AlibabaServiceOrDaoClassShouldEndWithImpl")
@Slf4j
public class DefaultNoticeService implements NoticeService {

	private final SmsConfig config;

	private final SmsAsyncConfig asyncConfig;

	private final ILoadBalancer<SendHandler, NoticeData> smsSenderLoadbalancer;

	private final SendAsyncThreadPoolExecutor executor;

	public DefaultNoticeService(SmsConfig config, SmsAsyncConfig asyncConfig,
			ILoadBalancer<SendHandler, NoticeData> smsSenderLoadbalancer,
			@Nullable SendAsyncThreadPoolExecutor executor) {
		this.config = config;
		this.asyncConfig = asyncConfig;
		this.smsSenderLoadbalancer = smsSenderLoadbalancer;
		this.executor = executor;
	}

	@Override
	public boolean phoneRegValidation(String phone) {
		return StringUtils.isNotBlank(phone) && (StringUtils.isBlank(config.getReg()) || phone.matches(
				config.getReg()));
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
		if (!asyncConfig.isEnable() || executor == null) {
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
		}
		else {
			try {
				result.result = sendHandler.send(noticeData, phones);
			}
			catch (RuntimeException e) {
				result.exception = e;
			}
			catch (Exception e) {
				result.exception = new SmsException(e.getLocalizedMessage(), e);
			}
		}

		if (result.exception != null) {
			log.debug(result.exception.getLocalizedMessage());
		}

		return result;
	}

	private static class SendResult {

		boolean result;

		RuntimeException exception;
	}
}
