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

package net.guerlab.sms.qcloudv3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;

import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.exception.SendFailedException;
import net.guerlab.sms.server.handler.AbstractSendHandler;

/**
 * 腾讯云发送处理.
 *
 * @author guer
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Slf4j
public class QCloudV3SendHandler extends AbstractSendHandler<QCloudV3Properties> {

	private static final String SUCCESS_CODE = "OK";

	private final SmsClient sender;

	public QCloudV3SendHandler(QCloudV3Properties properties, ApplicationEventPublisher eventPublisher) {
		super(properties, eventPublisher);
		Credential credential = new Credential(properties.getSecretId(), properties.getSecretKey());
		sender = new SmsClient(credential, properties.getRegion());
	}

	public static <T> List<List<T>> split(@Nullable Collection<T> collection, int size) {
		final List<List<T>> result = new ArrayList<>();
		if (collection == null || collection.isEmpty()) {
			return result;
		}

		ArrayList<T> subList = new ArrayList<>(size);
		for (T t : collection) {
			if (subList.size() >= size) {
				result.add(subList);
				subList = new ArrayList<>(size);
			}
			subList.add(t);
		}
		result.add(subList);
		return result;
	}

	@Override
	public String getChannelName() {
		return "qCloudV3";
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

		return split(phones, 200).parallelStream()
				.allMatch(subPhones -> send0(noticeData, templateId, params, subPhones));
	}

	private boolean send0(NoticeData noticeData, String templateId, ArrayList<String> params,
			Collection<String> phones) {
		try {
			SendSmsRequest request = new SendSmsRequest();
			request.setSmsSdkAppid(properties.getSmsAppId());
			request.setSign(properties.getSmsSign());
			request.setTemplateID(templateId);
			request.setTemplateParamSet(params.toArray(new String[0]));
			request.setPhoneNumberSet(phones.toArray(new String[0]));

			SendSmsResponse result = sender.SendSms(request);

			if (result.getSendStatusSet() == null) {
				return false;
			}

			ArrayList<String> success = new ArrayList<>(result.getSendStatusSet().length);

			String phone;
			String code;
			String message;
			for (SendStatus sendStatus : result.getSendStatusSet()) {
				phone = sendStatus.getPhoneNumber();
				code = sendStatus.getCode();
				if (SUCCESS_CODE.equals(code)) {
					success.add(phone);
				}
				else {
					message = sendStatus.getMessage();
					log.debug("send fail[phone={}, code={}, errMsg={}]", phone, code, message);
					publishSendFailEvent(noticeData, Collections.singleton(phone), new SendFailedException(message));
				}
			}

			if (!success.isEmpty()) {
				publishSendSuccessEvent(noticeData, success);
			}

			return !success.isEmpty();
		}
		catch (Exception e) {
			log.debug(e.getMessage(), e);
			publishSendFailEvent(noticeData, phones, e);
		}

		return false;
	}
}
