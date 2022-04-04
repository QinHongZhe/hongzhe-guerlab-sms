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

package net.guerlab.sms.chinamobile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.exception.SendFailedException;
import net.guerlab.sms.core.utils.StringUtils;
import net.guerlab.sms.server.handler.AbstractSendHandler;

/**
 * 移动云发送处理.
 *
 * @author guer
 */
@Slf4j
public class ChinaMobileSendHandler extends AbstractSendHandler<ChinaMobileProperties> {

	private static final String BODY_TEMPLATE = "{\"ecName\":\"%s\",\"apId\":\"%s\",\"templateId\":\"%s\",\"mobiles\":\"%s\",\"params\":\"%s\",\"sign\":\"%s\",\"addSerial\":\"\",\"mac\":\"%s\"}";

	private final ObjectMapper objectMapper;

	private final RestTemplate restTemplate;

	/**
	 * 创建实例.
	 *
	 * @param properties     移动云短信配置
	 * @param eventPublisher ApplicationEventPublisher
	 * @param objectMapper   ObjectMapper
	 * @param restTemplate   RestTemplate
	 */
	public ChinaMobileSendHandler(ChinaMobileProperties properties, ApplicationEventPublisher eventPublisher,
			ObjectMapper objectMapper, RestTemplate restTemplate) {
		super(properties, eventPublisher);
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
	}

	/**
	 * 构造模板参数.
	 *
	 * @param params 参数列表
	 * @return 模板参数
	 */
	private static String buildTemplateParas(Collection<String> params) {
		if (params.isEmpty()) {
			return "[\"\"]";
		}

		boolean firstParam = true;
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (String param : params) {
			if (!firstParam) {
				builder.append(",");
			}
			builder.append("\"");
			builder.append(param);
			builder.append("\"");
			firstParam = false;
		}
		builder.append("]");

		return builder.toString();
	}

	private static String buildMac(@Nullable String ecName, @Nullable String apId, @Nullable String secretKey,
			@Nullable String templateId, @Nullable String mobiles,
			@Nullable String params, @Nullable String sign) {
		String origin = ecName + apId + secretKey + templateId + mobiles + params + sign;
		return DigestUtils.md5DigestAsHex(origin.getBytes(StandardCharsets.UTF_8));
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

		List<String> paramsOrder = properties.getParamsOrder(type);

		ArrayList<String> params = new ArrayList<>();

		if (!paramsOrder.isEmpty()) {
			Map<String, String> paramMap = noticeData.getParams();
			for (String paramName : paramsOrder) {
				String paramValue = paramMap.get(paramName);

				params.add(paramValue);
			}
		}

		StringBuilder receiverBuilder = new StringBuilder();
		for (String phone : phones) {
			if (StringUtils.isBlank(phone)) {
				continue;
			}
			receiverBuilder.append(phone.trim());
			receiverBuilder.append(",");
		}

		String mobiles = receiverBuilder.substring(0, receiverBuilder.length() - 1);
		String paramsString = buildTemplateParas(params);
		String body = buildRequestBody(mobiles, templateId, paramsString);

		try {
			HttpEntity<String> httpEntity = new HttpEntity<>(body, new HttpHeaders());

			ResponseEntity<String> httpResponse = restTemplate
					.exchange(properties.getUri(), HttpMethod.POST, httpEntity, String.class);

			if (httpResponse.getBody() == null) {
				log.debug("response body ie null");
				publishSendFailEvent(noticeData, phones, new SendFailedException("response body ie null"));
				return false;
			}

			String responseContent = httpResponse.getBody();

			log.debug("responseContent: {}", responseContent);

			ChinaMobileResult result = objectMapper.readValue(responseContent, ChinaMobileResult.class);

			boolean succeed = ChinaMobileResult.SUCCESS_RSPCOD.equals(result.getRspcod());
			if (succeed) {
				publishSendSuccessEvent(noticeData, phones);
			}
			else {
				publishSendFailEvent(noticeData, phones, new SendFailedException(result.getRspcod()));
			}
			return succeed;
		}
		catch (Exception e) {
			log.debug(e.getLocalizedMessage(), e);
			publishSendFailEvent(noticeData, phones, e);
			return false;
		}
	}

	private String buildRequestBody(String mobiles, String templateId, String paramsString) {
		if (StringUtils.isAnyBlank(mobiles, templateId)) {
			throw new SendFailedException("buildRequestBody(): mobiles or templateId is null.");
		}

		String ecName = StringUtils.trimToNull(properties.getEcName());
		String apId = StringUtils.trimToNull(properties.getApId());
		String secretKey = StringUtils.trimToNull(properties.getSecretKey());
		String sign = StringUtils.trimToNull(properties.getSign());
		String mac = buildMac(ecName, apId, secretKey, templateId, mobiles, paramsString, sign);

		String body = String
				.format(BODY_TEMPLATE, ecName, apId, templateId, mobiles, paramsString.replace("\"", "\\\""), sign,
						mac);

		return new String(Base64.getEncoder().encode(body.getBytes(StandardCharsets.UTF_8)));
	}

	@Override
	public String getChannelName() {
		return "chinaMobile";
	}
}
