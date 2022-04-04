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

package net.guerlab.sms.core.exception;

/**
 * 短信异常.
 *
 * @author guer
 */
@SuppressWarnings("unused")
public class SmsException extends RuntimeException {

	/**
	 * 创建实例.
	 */
	public SmsException() {
	}

	/**
	 * 创建实例.
	 *
	 * @param message the detail message.
	 */
	public SmsException(String message) {
		super(message);
	}

	/**
	 * 创建实例.
	 *
	 * @param message the detail message.
	 * @param cause   the cause.  (A {@code null} value is permitted,
	 *                and indicates that the cause is nonexistent or unknown.)
	 */
	public SmsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 创建实例.
	 *
	 * @param cause the cause.  (A {@code null} value is permitted,
	 *              and indicates that the cause is nonexistent or unknown.)
	 */
	public SmsException(Throwable cause) {
		super(cause);
	}

	/**
	 * 创建实例.
	 *
	 * @param message            the detail message.
	 * @param cause              the cause.  (A {@code null} value is permitted,
	 *                           and indicates that the cause is nonexistent or unknown.)
	 * @param enableSuppression  whether or not suppression is enabled
	 *                           or disabled
	 * @param writableStackTrace whether or not the stack trace should
	 *                           be writable
	 */
	public SmsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
