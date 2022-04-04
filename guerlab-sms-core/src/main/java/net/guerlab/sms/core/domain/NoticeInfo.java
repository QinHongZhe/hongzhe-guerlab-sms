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

import java.util.Collection;

import lombok.Data;

/**
 * 通知信息.
 *
 * @author guer
 */
@Data
public class NoticeInfo {

	/**
	 * 通知内容.
	 */
	private NoticeData noticeData;

	/**
	 * 号码列表.
	 */
	private Collection<String> phones;
}
