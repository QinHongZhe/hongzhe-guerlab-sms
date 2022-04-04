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

package net.guerlab.sms.server.properties;

import lombok.Data;

/**
 * 验证码内存储存配置.
 *
 * @author guer
 */
@Data
public class VerificationCodeMemoryRepositoryConfig {

	/**
	 * 默认gc频率，单位秒.
	 */
	public static final long DEFAULT_GC_FREQUENCY = 300L;

	/**
	 * gc频率，单位秒.
	 */
	private long gcFrequency = DEFAULT_GC_FREQUENCY;
}
