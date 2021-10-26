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
package net.guerlab.sms.baiducloud;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.sms.SmsClient;
import com.baidubce.services.sms.SmsClientConfiguration;
import com.baidubce.services.sms.model.SendMessageV3Request;
import com.baidubce.services.sms.model.SendMessageV3Response;
import lombok.extern.slf4j.Slf4j;
import net.guerlab.sms.core.domain.NoticeData;
import net.guerlab.sms.core.exception.SendFailedException;
import net.guerlab.sms.core.utils.StringUtils;
import net.guerlab.sms.server.handler.AbstractSendHandler;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collection;

/**
 * 百度云发送处理
 *
 * @author guer
 */
@Slf4j
public class BaiduCloudSendHandler extends AbstractSendHandler<BaiduCloudProperties> {

    private final SmsClient client;

    public BaiduCloudSendHandler(BaiduCloudProperties properties, ApplicationEventPublisher eventPublisher) {
        super(properties, eventPublisher);

        SmsClientConfiguration config = new SmsClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(properties.getAccessKeyId(), properties.getSecretAccessKey()));
        config.setEndpoint(properties.getEndpoint());
        client = new SmsClient(config);
    }

    @Override
    public String getChannelName() {
        return "baiduCloud";
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

        SendMessageV3Request request = new SendMessageV3Request();
        request.setMobile(StringUtils.join(phones, ","));
        request.setSignatureId(properties.getSignatureId());
        request.setTemplate(templateId);
        request.setContentVar(noticeData.getParams());

        SendMessageV3Response response = client.sendMessage(request);

        if (response == null) {
            log.debug("send fail: empty response");
            publishSendFailEvent(noticeData, phones, new SendFailedException("empty response"));
            return false;
        } else if (!response.isSuccess()) {
            log.debug("send fail: [code:{}, message:{}]", response.getCode(), response.getMessage());
            publishSendFailEvent(noticeData, phones, new SendFailedException(response.getMessage()));
            return false;
        }

        publishSendSuccessEvent(noticeData, phones);
        return true;
    }
}
