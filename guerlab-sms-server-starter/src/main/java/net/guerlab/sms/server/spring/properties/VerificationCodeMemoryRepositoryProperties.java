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

package net.guerlab.sms.server.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import net.guerlab.sms.server.properties.VerificationCodeMemoryRepositoryConfig;

/**
 * 验证码内存储存配置.
 *
 * @author guer
 */
@ConfigurationProperties(prefix = VerificationCodeMemoryRepositoryProperties.PREFIX)
public class VerificationCodeMemoryRepositoryProperties extends VerificationCodeMemoryRepositoryConfig {

	/**
	 * 配置前缀.
	 */
	public static final String PREFIX = "sms.verification-code.repository.memory";
}
