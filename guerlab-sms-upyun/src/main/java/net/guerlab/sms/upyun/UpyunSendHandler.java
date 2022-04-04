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

package net.guerlab.sms.upyun;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.exception.SendFailedException;
import net.guerlab.sms.core.utils.StringUtils;
import net.guerlab.sms.server.handler.AbstractSendHandler;

/**
 * 又拍云发送处理.
 *
 * @author guer
 */
@Slf4j
public class UpyunSendHandler extends AbstractSendHandler<UpyunProperties> {

	private static final String API_URL = "https://sms-api.upyun.com/api/messages";

	private final ObjectMapper objectMapper;

	private final RestTemplate restTemplate;

	public UpyunSendHandler(UpyunProperties properties, ApplicationEventPublisher eventPublisher,
			ObjectMapper objectMapper, RestTemplate restTemplate) {
		super(properties, eventPublisher);
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
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

		ArrayList<String> params = buildParams(noticeData);

		UpyunSendRequest request = new UpyunSendRequest();
		request.setMobile(StringUtils.join(phones, ","));
		request.setTemplateId(templateId);
		request.setVars(StringUtils.join(params, "|"));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(HttpHeaders.AUTHORIZATION, properties.getToken());

		try {
			HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);

			ResponseEntity<String> httpResponse = restTemplate
					.exchange(API_URL, HttpMethod.POST, httpEntity, String.class);

			if (httpResponse.getBody() == null) {
				log.debug("response body ie null");
				publishSendFailEvent(noticeData, phones, new SendFailedException("response body ie null"));
				return false;
			}

			String responseContent = httpResponse.getBody();

			boolean isJson = responseContent.startsWith("{") && responseContent.endsWith("}");
			boolean sendFail = !responseContent.contains("message_ids");
			if (!isJson || sendFail) {
				log.debug("send fail: {}", responseContent);
				publishSendFailEvent(noticeData, phones, new SendFailedException(responseContent));
				return false;
			}

			log.debug("responseContent: {}", responseContent);

			UpyunSendResult result = objectMapper.readValue(responseContent, UpyunSendResult.class);

			Collection<MessageId> messageIds = result.getMessageIds();

			if (messageIds == null || messageIds.isEmpty()) {
				publishSendFailEvent(noticeData, phones, new SendFailedException("empty messageIds list"));
				return false;
			}

			boolean succeed = messageIds.stream().filter(Objects::nonNull).anyMatch(MessageId::succeed);

			if (succeed) {
				publishSendSuccessEvent(noticeData, phones);
			}
			else {
				publishSendFailEvent(noticeData, phones, new SendFailedException("templateId invalid"));
			}

			return succeed;
		}
		catch (Exception e) {
			log.debug(e.getLocalizedMessage(), e);
			publishSendFailEvent(noticeData, phones, e);
			return false;
		}
	}

	private ArrayList<String> buildParams(NoticeData noticeData) {
		List<String> paramsOrder = properties.getParamsOrder(noticeData.getType());

		ArrayList<String> params = new ArrayList<>();

		if (!paramsOrder.isEmpty()) {
			Map<String, String> paramMap = noticeData.getParams();
			for (String paramName : paramsOrder) {
				String paramValue = paramMap.get(paramName);

				params.add(paramValue);
			}
		}

		return params;
	}

	@Override
	public String getChannelName() {
		return "upyun";
	}
}
