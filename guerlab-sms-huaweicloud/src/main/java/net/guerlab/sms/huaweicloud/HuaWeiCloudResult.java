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

package net.guerlab.sms.huaweicloud;

import java.util.Collection;

import lombok.Data;

/**
 * 响应结果.
 *
 * @author guer
 */
@Data
public class HuaWeiCloudResult {

	/**
	 * 成功代码.
	 */
	public static final String SUCCESS_CODE = "000000";

	/**
	 * 请求返回的结果码.
	 */
	private String code;

	/**
	 * 请求返回的结果码描述.
	 */
	private String description;

	/**
	 * 短信ID列表，当目的号码存在多个时，每个号码都会返回一个SmsID.
	 * <p>
	 * 当返回异常响应时不携带此字段。
	 */
	private Collection<SmsID> result;

	/**
	 * 短信ID.
	 */
	@Data
	public static class SmsID {

		/**
		 * 短信的唯一标识.
		 */
		private String smsMsgId;

		/**
		 * 短信发送方的号码.
		 */
		private String from;

		/**
		 * 短信接收方的号码.
		 */
		private String originTo;

		/**
		 * 短信状态码.
		 */
		private String status;

		/**
		 * 短信资源的创建时间，即短信平台接收到客户发送短信请求的时间，为UTC时间.
		 * <p>
		 * 格式为：yyyy-MM-dd'T'HH:mm:ss'Z'。
		 */
		private String createTime;
	}
}
