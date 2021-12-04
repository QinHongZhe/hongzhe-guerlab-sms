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
package net.guerlab.sms.server.spring.properties;

import net.guerlab.sms.server.properties.VerificationCodeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 验证码配置
 *
 * @author guer
 */
@ConfigurationProperties(prefix = VerificationCodeProperties.PREFIX)
public class VerificationCodeProperties extends VerificationCodeConfig {

    public static final String PREFIX = "sms.verification-code";
}
