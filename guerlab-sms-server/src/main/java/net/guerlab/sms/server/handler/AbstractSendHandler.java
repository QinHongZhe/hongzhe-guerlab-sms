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

package net.guerlab.sms.server.handler;

import java.util.Collection;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;

import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.handler.SendHandler;
import net.guerlab.sms.server.entity.SmsSendFailEvent;
import net.guerlab.sms.server.entity.SmsSendFinallyEvent;
import net.guerlab.sms.server.entity.SmsSendSuccessEvent;
import net.guerlab.sms.server.properties.AbstractHandlerProperties;

/**
 * 抽象发送处理.
 *
 * @param <P> 配置类类型
 * @author guer
 */
public abstract class AbstractSendHandler<P extends AbstractHandlerProperties<?>> implements SendHandler {

	/**
	 * 配置.
	 */
	protected final P properties;

	private final ApplicationEventPublisher eventPublisher;

	public AbstractSendHandler(P properties, @Nullable ApplicationEventPublisher eventPublisher) {
		this.properties = properties;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public boolean acceptSend(@Nullable String type) {
		return properties.getTemplates().containsKey(type);
	}

	/**
	 * 获取通道名称.
	 *
	 * @return 通道名称
	 */
	public abstract String getChannelName();

	/**
	 * 发布发送成功事件.
	 *
	 * @param noticeData 通知内容
	 * @param phones     手机号列表
	 */
	protected final void publishSendSuccessEvent(NoticeData noticeData, Collection<String> phones) {
		if (eventPublisher == null) {
			return;
		}
		eventPublisher.publishEvent(
				new SmsSendSuccessEvent(this, getChannelName(), phones, noticeData.getType(), noticeData.getParams()));
		publishSendFinallyEvent(noticeData, phones);
	}

	/**
	 * 发布发送失败事件.
	 *
	 * @param noticeData 通知内容
	 * @param phones     手机号列表
	 * @param cause      源异常
	 */
	protected final void publishSendFailEvent(NoticeData noticeData, Collection<String> phones, Throwable cause) {
		if (eventPublisher == null) {
			return;
		}
		eventPublisher.publishEvent(
				new SmsSendFailEvent(this, getChannelName(), phones, noticeData.getType(), noticeData.getParams(),
						cause));
		publishSendFinallyEvent(noticeData, phones);
	}

	/**
	 * 发布发送结束事件.
	 *
	 * @param noticeData 通知内容
	 * @param phones     手机号列表
	 */
	private void publishSendFinallyEvent(NoticeData noticeData, Collection<String> phones) {
		if (eventPublisher == null) {
			return;
		}
		eventPublisher.publishEvent(
				new SmsSendFinallyEvent(this, getChannelName(), phones, noticeData.getType(), noticeData.getParams()));
	}
}
