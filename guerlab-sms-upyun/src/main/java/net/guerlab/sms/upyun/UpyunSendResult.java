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

package net.guerlab.sms.upyun;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 发送响应.
 *
 * @author guer
 */
@Data
public class UpyunSendResult {

	/**
	 * 所有手机号发送短信的结果.
	 */
	@JsonProperty("message_ids")
	private Collection<MessageId> messageIds;
}
