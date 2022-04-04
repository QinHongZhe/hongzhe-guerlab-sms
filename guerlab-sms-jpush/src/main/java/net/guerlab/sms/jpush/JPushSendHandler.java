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

package net.guerlab.sms.jpush;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;

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
import net.guerlab.sms.server.handler.AbstractSendHandler;

/**
 * 极光发送处理.
 *
 * @author guer
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Slf4j
public class JPushSendHandler extends AbstractSendHandler<JPushProperties> {

	private final ObjectMapper objectMapper;

	private final RestTemplate restTemplate;

	/**
	 * 创建实例.
	 *
	 * @param properties     极光短信配置
	 * @param eventPublisher ApplicationEventPublisher
	 * @param objectMapper   ObjectMapper
	 * @param restTemplate   RestTemplate
	 */
	public JPushSendHandler(JPushProperties properties, ApplicationEventPublisher eventPublisher,
			ObjectMapper objectMapper, RestTemplate restTemplate) {
		super(properties, eventPublisher);
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
	}

	@Override
	public boolean send(NoticeData noticeData, Collection<String> phones) {
		String type = noticeData.getType();

		Integer templateId = properties.getTemplates(type);

		if (templateId == null) {
			log.debug("templateId invalid");
			publishSendFailEvent(noticeData, phones, new SendFailedException("templateId invalid"));
			return false;
		}

		String[] phoneArray = phones.toArray(new String[] {});

		try {
			Result result;
			if (phoneArray.length > 1) {
				MultiRecipient data = new MultiRecipient();
				data.setSignId(properties.getSignId());
				data.setTempId(templateId);

				ArrayList<Recipient> recipients = new ArrayList<>(phoneArray.length);
				for (String phone : phoneArray) {
					Recipient recipient = new Recipient();
					recipient.setMobile(phone);
					recipient.setTempPara(noticeData.getParams());
					recipients.add(recipient);
				}

				data.setRecipients(recipients);

				result = getResponse("https://api.sms.jpush.cn/v1/messages/batch", data, MultiResult.class);
			}
			else {
				Recipient data = new Recipient();
				data.setMobile(phoneArray[0]);
				data.setSignId(properties.getSignId());
				data.setTempId(templateId);
				data.setTempPara(noticeData.getParams());

				result = getResponse("https://api.sms.jpush.cn/v1/messages", data, SingleResult.class);
			}

			if (result.getError() == null) {
				publishSendSuccessEvent(noticeData, phones);
				return true;
			}
			else {
				publishSendFailEvent(noticeData, phones, new SendFailedException(result.getError().getMessage()));
				return false;
			}
		}
		catch (Exception e) {
			log.debug(e.getLocalizedMessage(), e);
			publishSendFailEvent(noticeData, phones, e);
		}

		return false;
	}

	private <T> T getResponse(String uri, Object requestData, Class<T> clazz) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(HttpHeaders.AUTHORIZATION, "Basic " + getSign());

		HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(requestData), headers);

		ResponseEntity<String> httpResponse = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);

		if (httpResponse.getBody() == null) {
			log.debug("response body ie null");
			throw new SendFailedException("response body ie null");
		}

		String responseContent = httpResponse.getBody();

		log.debug("responseContent: {}", responseContent);

		return objectMapper.readValue(responseContent, clazz);
	}

	private String getSign() {
		String origin = properties.getAppKey() + ":" + properties.getMasterSecret();
		return Base64.getEncoder().encodeToString(origin.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String getChannelName() {
		return "jPush";
	}
}
