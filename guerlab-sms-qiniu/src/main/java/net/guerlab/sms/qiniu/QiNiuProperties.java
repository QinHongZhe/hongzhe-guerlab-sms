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

package net.guerlab.sms.qiniu;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.boot.context.properties.ConfigurationProperties;

import net.guerlab.sms.server.properties.AbstractHandlerProperties;

/**
 * 七牛云短信配置.
 *
 * @author guer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "sms.qiniu")
public class QiNiuProperties extends AbstractHandlerProperties<String> {

	/**
	 * accessKey.
	 */
	private String accessKey;

	/**
	 * secretKey.
	 */
	private String secretKey;

}
