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

package net.guerlab.sms.yunpian;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;

import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.exception.SendFailedException;
import net.guerlab.sms.core.utils.StringUtils;
import net.guerlab.sms.server.handler.AbstractSendHandler;

/**
 * 云片网发送处理.
 *
 * @author guer
 */
@Slf4j
public class YunPianSendHandler extends AbstractSendHandler<YunPianProperties> {

	private final YunpianClient client;

	public YunPianSendHandler(YunPianProperties properties, ApplicationEventPublisher eventPublisher) {
		super(properties, eventPublisher);
		client = new YunpianClient(properties.getApikey()).init();
	}

	@Override
	public String getChannelName() {
		return "yunPian";
	}

	@Override
	public boolean send(NoticeData noticeData, Collection<String> phones) {
		String type = noticeData.getType();

		String templateId = properties.getTemplates(type);

		if (templateId == null) {
			log.debug("templateId invalid");
			publishSendFailEvent(noticeData, phones, new SendFailedException("templateId invalid"));
			return false;
		}

		Map<String, String> params = noticeData.getParams();
		StringBuilder paramsStringBuilder = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			boolean firstParam = true;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (!firstParam) {
					paramsStringBuilder.append("&");
				}
				paramsStringBuilder.append(getEncodeValue("#" + entry.getKey() + "#"));
				paramsStringBuilder.append("=");
				paramsStringBuilder.append(getEncodeValue(entry.getValue()));
				firstParam = false;
			}
		}

		String mobileString = StringUtils.join(phones, ",");

		Map<String, String> data = new HashMap<>(8);
		data.put("apikey", properties.getApikey());
		data.put("mobile", mobileString);
		data.put("tpl_id", templateId);
		data.put("tpl_value", paramsStringBuilder.toString());

		Result<?> result;

		if (phones.size() > 1) {
			//noinspection deprecation
			result = client.sms().tpl_batch_send(data);
		}
		else {
			//noinspection deprecation
			result = client.sms().tpl_single_send(data);
		}

		boolean succeed = Objects.equals(result.getCode(), 0);
		if (succeed) {
			publishSendSuccessEvent(noticeData, phones);
		}
		else {
			log.debug("send fail: {}", result.getMsg());
			publishSendFailEvent(noticeData, phones, new SendFailedException(result.getMsg()));
		}
		return succeed;
	}

	private String getEncodeValue(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
		}
		catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage(), e);
		}
	}
}
