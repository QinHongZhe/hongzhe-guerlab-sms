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
package net.guerlab.sms.qcloudv3;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.guerlab.sms.server.properties.AbstractHandlerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 腾讯云短信配置
 *
 * @author guer
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "sms.qcloud-v3")
public class QCloudV3Properties extends AbstractHandlerProperties<String> {

    /**
     * 腾讯云 SecretID
     */
    private String secretId;

    /**
     * 腾讯云 SecretKey
     */
    private String secretKey;

    /**
     * 短信签名
     */
    private String region;

    /**
     * 短信应用ID
     */
    private String smsAppId;

    /**
     * 短信签名
     */
    private String smsSign;

}
