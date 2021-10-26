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
package net.guerlab.sms.jdcloud;

import com.jdcloud.sdk.auth.CredentialsProvider;
import com.jdcloud.sdk.auth.StaticCredentialsProvider;
import com.jdcloud.sdk.http.HttpRequestConfig;
import com.jdcloud.sdk.http.Protocol;
import com.jdcloud.sdk.service.sms.client.SmsClient;
import com.jdcloud.sdk.service.sms.model.BatchSendRequest;
import com.jdcloud.sdk.service.sms.model.BatchSendResult;
import lombok.extern.slf4j.Slf4j;
import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.exception.SendFailedException;
import net.guerlab.sms.server.handler.AbstractSendHandler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 京东云发送处理
 *
 * @author guer
 */
@Slf4j
public class JdCloudSendHandler extends AbstractSendHandler<JdCloudProperties> {

    private final SmsClient smsClient;

    public JdCloudSendHandler(JdCloudProperties properties, ApplicationEventPublisher eventPublisher) {
        super(properties, eventPublisher);
        CredentialsProvider credentialsProvider = new StaticCredentialsProvider(properties.getAccessKeyId(),
                properties.getSecretAccessKey());
        smsClient = SmsClient.builder().credentialsProvider(credentialsProvider)
                .httpRequestConfig(new HttpRequestConfig.Builder().protocol(Protocol.HTTP).build()).build();
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

        BatchSendRequest request = new BatchSendRequest();
        request.setRegionId(properties.getRegion());
        request.setTemplateId(templateId);
        request.setSignId(properties.getSignId());
        request.setPhoneList(new ArrayList<>(phones));
        request.setParams(params);
        BatchSendResult result = smsClient.batchSend(request).getResult();
        Boolean status = result.getStatus();
        boolean flag = status != null && status;

        if (flag) {
            publishSendSuccessEvent(noticeData, phones);
        } else {
            log.debug("send fail [code:{}, message:{}]", result.getCode(), result.getMessage());
            publishSendFailEvent(noticeData, phones, new SendFailedException(result.getMessage()));
        }

        return flag;
    }

    @Override
    public boolean acceptSend(@Nullable String type) {
        return properties.getTemplates().containsKey(type);
    }

    @Override
    public String getChannelName() {
        return "jdCloud";
    }
}
