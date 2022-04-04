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

package net.guerlab.sms.core.domain;

import java.util.Map;

import lombok.Data;

/**
 * 通知内容.
 *
 * @author guer
 */
@Data
public class NoticeData {

	/**
	 * 类型.
	 */
	private String type;

	/**
	 * 参数列表.
	 */
	private Map<String, String> params;
}
